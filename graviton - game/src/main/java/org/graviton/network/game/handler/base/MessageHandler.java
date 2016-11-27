package org.graviton.network.game.handler.base;

import lombok.extern.slf4j.Slf4j;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.handler.AccountHandler;
import org.graviton.network.game.handler.GameHandler;

/**
 * Created by Botan on 05/11/2016 : 01:01
 */
@Slf4j
public class MessageHandler {
    private final AccountHandler accountHandler;
    private final GameHandler gameHandler;

    private final GameClient client;

    public MessageHandler(GameClient gameClient) {
        this.client = gameClient;
        this.accountHandler = new AccountHandler(client);
        this.gameHandler = new GameHandler(client);
    }

    public void handle(String data) {
        switch ((byte) data.charAt(0)) {
            case 65: //Account ('A')
                this.accountHandler.handle(data.substring(2), (byte) data.charAt(1));
                break;

            case 71: //Game ('G')
                this.gameHandler.handle(data.substring(2), (byte) data.charAt(1));
                break;

            default:
                log.error("not implemented packet {}", data);
        }

    }
}
