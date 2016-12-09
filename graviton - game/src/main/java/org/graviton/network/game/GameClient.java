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
import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.network.game.handler.base.MessageHandler;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Botan on 04/11/2016 : 22:50
 */
@Data
public class GameClient {
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
        send(GamePacketFormatter.helloGameMessage());
    }

    public void send(String data) {
        session.write(data);
    }

    void disconnect() {
        this.player.getGameMap().out(player);

        this.playerRepository.save(account);
        this.accountRepository.unload(account.getId());
        this.session.closeNow();
    }



    public void setLanguage(String language) {
        this.language = Language.get(language);
    }



    public void createGame() {
        String currentAddress = ((InetSocketAddress) this.session.getRemoteAddress()).getAddress().getHostAddress();

        send(GamePacketFormatter.gameCreationSuccessMessage());

        send(MessageFormatter.welcomeMessage());
        send(MessageFormatter.lastInformationMessage(account.getLastConnection(), account.getLastAddress()));
        send(MessageFormatter.actualInformationMessage(currentAddress));

        account.setLastConnection(new SimpleDateFormat("yyyy~MM~dd~HH~mm").format(new Date()));
        account.setLastAddress(currentAddress);

        accountRepository.updateInformation(account);
    }

    public void sendGameInformation() {
        player.getGameMap().enter(player);
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
                player.send(MessageFormatter.privateMessage(this.player.getId(), this.player.getName(), data[1], true));
                send(MessageFormatter.privateMessage(player.getId(), player.getName(), data[1], false));
            } else
                send(MessageFormatter.notConnectedPlayerMessage(data[0]));
        } else
            speak(channel, MessageFormatter.buildChannelMessage(channel.value(), player, data[1]));
    }

    private void speak(Channel channel, String generatedPacket) {
        switch (channel) {
            case General:
                player.getGameMap().send(generatedPacket);
                break;

            case Trade:
            case Recruitment:
                playerRepository.send(generatedPacket);
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

    public void changeOrientation(byte orientation) {
        player.getGameMap().send(GamePacketFormatter.changeOrientationMessage(player.getId(), orientation));
        player.getLocation().setOrientation(OrientationEnum.valueOf(orientation));
    }

    public final MessageHandler getBaseHandler() {
        return ((MessageHandler) session.getAttribute((byte) 1));
    }

}
