package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;
import org.graviton.game.client.account.Account;
import org.graviton.game.client.player.Player;
import org.graviton.game.interaction.InteractionManager;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Botan on 22/11/2016 : 22:10
 */
@Slf4j
public class GameHandler {

    private final GameClient client;
    private final InteractionManager interactionManager;
    private final IoSession session;

    public GameHandler(GameClient client) {
        this.client = client;
        this.interactionManager = client.getInteractionManager();
        this.session = client.getSession();
    }

    public void handle(String data, byte subHeader) { // 'G'
        switch (subHeader) {
            case 65: // 'A'
                createAction(Short.parseShort(data.substring(0, 3)), data.substring(3));
                break;

            case 67: // 'C'
                createGame(client.getAccount());
                break;

            case 73: // 'I'
                sendGameInformation(client.getPlayer());
                break;

            case 75: //'K'
                finishAction(data);
                break;

            default:
                log.error("not implemented game packet '{}'", (char) subHeader);
        }

    }


    private void createGame(Account account) {
        String currentAddress = ((InetSocketAddress) this.session.getRemoteAddress()).getAddress().getHostAddress();

        client.send(GamePacketFormatter.gameCreationSuccessMessage());

        client.send(MessageFormatter.welcomeMessage());
        client.send(MessageFormatter.lastInformationMessage(account.getLastConnection(), account.getLastAddress()));
        client.send(MessageFormatter.actualInformationMessage(currentAddress));

        account.setLastConnection(new SimpleDateFormat("yyyy~MM~dd~HH~mm").format(new Date()));
        account.setLastAddress(currentAddress);

        client.getAccountRepository().updateInformation(account);
    }

    private void sendGameInformation(Player player) {
        player.getGameMap().enter(player);
    }

    private void createAction(short id, String arguments) {
        this.interactionManager.create(id, arguments);
    }

    private void finishAction(String data) {
        interactionManager.end(interactionManager.pollLast(), data.charAt(0) == 'K', data.substring(1));
    }

}
