package org.graviton.network.game.handler;

import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.graviton.database.entity.EntityFactory;
import org.graviton.database.repository.AccountRepository;
import org.graviton.database.repository.PlayerRepository;
import org.graviton.game.client.account.Account;
import org.graviton.game.client.player.Player;
import org.graviton.game.guild.Guild;
import org.graviton.game.house.House;
import org.graviton.game.items.Item;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.*;
import org.graviton.utils.Utils;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.graviton.utils.Utils.randomPseudo;

/**
 * Created by Botan on 22/11/2016 : 21:39
 */
@Slf4j
public class AccountHandler {
    private final GameClient client;
    private Account account;
    private PlayerRepository playerRepository;
    private AccountRepository accountRepository;
    private EntityFactory entityFactory;

    public AccountHandler(GameClient client) {
        this.client = client;
        this.playerRepository = client.getPlayerRepository();
        this.accountRepository = client.getAccountRepository();
        this.entityFactory = client.getEntityFactory();
    }

    public void initialize() {
        this.account = client.getAccount();
    }

    public void handle(String data, char subHeader) {
        switch (subHeader) {
            case 'A':
                createPlayer(data);
                break;

            case 'B':
                this.client.getPlayer().boostStatistics(Byte.parseByte(data));
                break;

            case 'D':
                deletePlayer(data.split("\\|"));
                break;

            case 'G':
                attributeGift(data.split("\\|"));
                break;

            case 'L':
                client.send(client.getAccount().getPlayerPacket(data.isEmpty()));
                break;

            case 'P':
                client.send(GamePacketFormatter.playerNameSuggestionSuccessMessage(randomPseudo()));
                break;

            case 'S':
                selectPlayer(Integer.parseInt(data));
                break;

            case 'T':
                applyTicket(Integer.parseInt(data));
                break;

            case 'V':
                client.send(GamePacketFormatter.requestRegionalVersionMessage());
                break;

            case 'f':
                client.send(GamePacketFormatter.getQueuePositionMessage());
                break;

            case 'g':
                client.setLanguage(data);
                applyGifts();
                break;

            default:
                log.error("not implemented account packet '{}'", subHeader);
        }

    }

    private void createPlayer(String data) {
        Player player = new Player(playerRepository.getNextId(), data, this.account, entityFactory);
        this.account.getPlayers().add(player);
        client.send(this.account.getPlayerPacket(false));
        playerRepository.create(player);
    }

    private void selectPlayer(int playerId) {
        Player player = account.getPlayer(playerId);
        client.setPlayer(player);

        player.setOnline(true);
        client.send(PlayerPacketFormatter.askMessage(player));
        client.send(SpellPacketFormatter.spellListMessage(player.getSpellList()));
        client.send(GamePacketFormatter.addChannelsMessage(account.getChannels()));

        client.send(PlayerPacketFormatter.alignmentMessage(player.getAlignment().getId()));
        client.send(PlayerPacketFormatter.restrictionMessage());
        client.send(PlayerPacketFormatter.podsMessage(player.getPods()));

        player.getEntityFactory().getPlayerRepository().removeMerchant(player.getId());

        System.err.println(player.getFight() == null);

        if (player.getFight() == null)
            player.getMap().load(player);
        else
            player.getFight().getGameMap().loadOnlyData(player, client);

        String currentAddress = ((InetSocketAddress) this.client.getSession().getRemoteAddress()).getAddress().getHostAddress();

        client.send(MessageFormatter.welcomeMessage());

        if (!account.getLastConnection().isEmpty() && !account.getLastAddress().isEmpty()) {
            client.send(MessageFormatter.lastInformationMessage(account.getLastConnection(), account.getLastAddress()));
            client.send(MessageFormatter.actualInformationMessage(currentAddress));
        }

        Guild guild;
        if ((guild = player.getGuild()) != null)
            client.send(GuildPacketFormatter.gsMessage(guild.getMember(playerId).setPlayer(player)));

        account.setLastAddress(currentAddress);
    }

    private void deletePlayer(String[] data) {
        int player = Integer.parseInt(data[0]);
        String secretAnswer = data.length > 1 ? data[1] : "";
        if (secretAnswer.isEmpty() || secretAnswer.equals(account.getAnswer())) {
            playerRepository.remove(account.getPlayer(player));
            client.send(account.getPlayerPacket(false));
        } else
            client.send(GamePacketFormatter.playerDeleteFailedMessage());
    }

    private void applyTicket(int accountId) {
        Account account = this.accountRepository.load(accountId);

        if (account != null) {
            client.setAccount(account);
            account.setClient(client);
            this.account = account;
            client.send(GamePacketFormatter.accountTicketSuccessMessage("0"));
        } else {
            client.send(GamePacketFormatter.accountTicketErrorMessage());
        }
    }

    private void applyGifts() {
        if (client.getAccount().getGifts() != null)
            client.send(ItemPacketFormatter.giftMessage(client.getAccount().getGifts(), client.getLanguage()));
    }

    private void attributeGift(String[] data) {
        short template = Short.parseShort(data[0]);
        int playerId = Integer.parseInt(data[1]);


        Pair<ItemTemplate, Short> gift = client.getAccount().getGift(template);

        Item item = gift.getKey().createMax(entityFactory.getNextItemId());
        item.setQuantity(gift.getValue());

        account.getPlayer(playerId).getInventory().addItem(item, true);
        account.getGifts().remove(gift);

        entityFactory.getAccountRepository().updateInformation(client.getAccount());

        if (account.getGifts().isEmpty())
            client.send(ItemPacketFormatter.giftAttributionSuccessMessage());
        else
            applyGifts();
    }

}
