package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.database.entity.EntityFactory;
import org.graviton.database.repository.AccountRepository;
import org.graviton.database.repository.PlayerRepository;
import org.graviton.game.client.account.Account;
import org.graviton.game.client.player.Player;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;
import org.graviton.network.game.protocol.SpellPacketFormatter;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public void handle(String data, byte subHeader) { // 'A'
        switch (subHeader) {
            case 65: // 'A'
                createPlayer(data);
                break;

            case 66: // 'B'
                this.client.getPlayer().boostStatistics(Byte.parseByte(data));
                break;

            case 68: // 'D'
                deletePlayer(data.split("\\|"));
                break;

            case 76: // 'L'
                client.send(client.getAccount().getPlayerPacket(true));
                break;

            case 80: // 'P'
                client.send(GamePacketFormatter.playerNameSuggestionSuccessMessage(randomPseudo()));
                break;

            case 83: // 'S'
                selectPlayer(Integer.parseInt(data));
                break;

            case 84: // 'T'
                applyTicket(Integer.parseInt(data));
                break;

            case 86: // 'V'
                client.send(GamePacketFormatter.requestRegionalVersionMessage());
                break;

            case 102: // 'f'
                client.send(GamePacketFormatter.getQueuePositionMessage());
                break;

            case 103: // 'g'
                client.setLanguage(data);
                break;

            default:
                log.error("not implemented account packet '{}'", (char) subHeader);
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
        player.getMap().load(player);

        String currentAddress = ((InetSocketAddress) this.client.getSession().getRemoteAddress()).getAddress().getHostAddress();

        client.send(MessageFormatter.welcomeMessage());

        if (!account.getLastConnection().isEmpty() && !account.getLastAddress().isEmpty()) {
            client.send(MessageFormatter.lastInformationMessage(account.getLastConnection(), account.getLastAddress()));
            client.send(MessageFormatter.actualInformationMessage(currentAddress));
        }

        account.setLastConnection(new SimpleDateFormat("yyyy~MM~dd~HH~mm").format(new Date()));
        account.setLastAddress(currentAddress);

        client.getAccountRepository().updateInformation(account);
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
}
