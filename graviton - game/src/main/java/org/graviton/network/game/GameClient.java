package org.graviton.network.game;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Data;
import org.apache.mina.core.session.IoSession;
import org.graviton.api.Language;
import org.graviton.database.entity.EntityFactory;
import org.graviton.database.repository.AccountRepository;
import org.graviton.database.repository.PlayerRepository;
import org.graviton.game.channel.Channel;
import org.graviton.game.client.account.Account;
import org.graviton.game.client.player.Player;
import org.graviton.game.interaction.InteractionManager;
import org.graviton.network.game.handler.base.MessageHandler;
import org.graviton.network.game.protocol.GameProtocol;
import org.graviton.network.game.protocol.MessageProtocol;
import org.graviton.network.game.protocol.PlayerProtocol;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Botan on 04/11/2016 : 22:50
 */
@Data
public class GameClient {
    private final MessageHandler messageHandler = new MessageHandler(this);
    private final InteractionManager interactionManager = new InteractionManager(this);

    private final long id;
    private final IoSession session;

    private Account account;
    private Player player;
    private Language language;

    @Inject
    private AccountRepository accountRepository;
    @Inject
    private PlayerRepository playerRepository;
    @Inject
    private EntityFactory entityFactory;


    public GameClient(IoSession session, Injector injector) {
        injector.injectMembers(this);
        this.id = session.getId();
        this.session = session;
        send(GameProtocol.helloGameMessage());
    }

    public void send(String data) {
        session.write(data);
    }

    public void handle(String data) {
        messageHandler.handle(data);
    }

    public void disconnect() {
        this.player.getGameMap().out(player);

        this.playerRepository.save(account);
        this.accountRepository.unload(account.getId());
        this.session.closeNow();
    }

    public void applyTicket(int accountId) {
        Account account = this.accountRepository.load(accountId);
        if (account != null) {
            account.setClient(this);
            this.account = account;
            send(GameProtocol.accountTicketSuccessMessage("0"));
        } else {
            send(GameProtocol.accountTicketErrorMessage());
        }
    }

    public void setLanguage(String language) {
        this.language = Language.get(language);
    }

    public void createPlayer(String data) {
        Player player = new Player(playerRepository.getNextId(), data, this.account, entityFactory);
        this.account.getPlayers().add(player);
        this.send(this.account.getPlayerPacket(false));
        playerRepository.create(player);
    }

    public void selectPlayer(int playerId) {
        Player player = (this.player = account.getPlayer(playerId));
        player.setOnline(true);
        send(PlayerProtocol.askMessage(player));
        send(PlayerProtocol.asMessage(player, entityFactory.getExperience(player.getLevel()), player.getAlignment(), player.getStatistics()));

        player.getGameMap().load(player);

        send(GameProtocol.regenTimerMessage((short) 2000));
        send(GameProtocol.addChannelsMessage(account.getChannels()));

        send(PlayerProtocol.alignmentMessage(player.getAlignment().getId()));
        send(PlayerProtocol.restrictionMessage());
        send(PlayerProtocol.podsMessage(player.getPods()));
    }

    public void createGame() {
        String currentAddress = ((InetSocketAddress) this.session.getRemoteAddress()).getAddress().getHostAddress();

        send(GameProtocol.gameCreationSuccessMessage());

        send(MessageProtocol.welcomeMessage());
        send(MessageProtocol.lastInformationsMessage(account.getLastConnection(), account.getLastAddress()));
        send(MessageProtocol.actualInformationMessage(currentAddress));

        account.setLastConnection(new SimpleDateFormat("yyyy~MM~dd~HH~mm").format(new Date()));
        account.setLastAddress(currentAddress);

        accountRepository.updateInformation(account);
    }

    public void sendGameInformation() {
        player.getGameMap().enter(player);
    }

    public void deletePlayer(String[] data) {
        int player = Integer.parseInt(data[0]);
        String secretAnswer = data.length > 1 ? data[1] : "";
        if (secretAnswer.isEmpty() || secretAnswer.equals(account.getAnswer())) {
            playerRepository.remove(account.getPlayer(player));
            send(account.getPlayerPacket(false));
        } else
            send(GameProtocol.playerDeleteFailedMessage());
    }

    public void createAction(short id, String arguments) {
        this.interactionManager.create(id, arguments);
    }

    public void finishAction(String data) {
        interactionManager.end(interactionManager.pollLast(), data.charAt(0) == 'K', data.substring(1));
    }

    public void changePlayerMapByPosition(String position) {
        player.changeMap(entityFactory.getMapByPosition(position));
    }

    public void objectMove(String data[]) {
        //Todo
    }

    public void speak(String[] data) {
        Channel channel = Channel.get(data[0].charAt(0));

        if (channel == null) {
            Player player = playerRepository.find(data[0]);
            if (player != null) {
                player.send(MessageProtocol.privateMessage(this.player.getId(), this.player.getName(), data[1], true));
                send(MessageProtocol.privateMessage(player.getId(), player.getName(), data[1], false));
            } else
                send(MessageProtocol.notConnectedPlayerMessage(data[0]));
        } else {
            String packet = MessageProtocol.buildChannelMessage(channel.value(), player, data[1]);

            switch (channel) {
                case General:
                    player.getGameMap().send(packet);
                    break;

                case Trade:
                case Recruitment:
                    playerRepository.send(packet);
                    break;

                case Admin:
                case Information:
                case Alignment:
                case Team:
                case Party:
                case Guild:
                    break;
            }
        }
    }

}
