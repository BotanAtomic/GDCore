package org.graviton.network.game.handler.base;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.handler.*;

/**
 * Created by Botan on 05/11/2016 : 01:01
 */
@Slf4j
@Data
public class MessageHandler {
    private final AccountHandler accountHandler;
    private final GameHandler gameHandler;
    private final BasicHandler basicHandler;
    private final ItemHandler itemHandler;
    private final EnvironmentHandler environmentHandler;
    private final DialogHandler dialogHandler;

    public MessageHandler(GameClient gameClient) {
        GameClient client = gameClient;
        this.accountHandler = new AccountHandler(client);
        this.gameHandler = new GameHandler(client);
        this.basicHandler = new BasicHandler(client);
        this.itemHandler = new ItemHandler(client);
        this.environmentHandler = new EnvironmentHandler(client);
        this.dialogHandler = new DialogHandler(client);
    }

    public void handle(String data) {
        switch ((byte) data.charAt(0)) {
            case 65: //Account ('A')
                this.accountHandler.handle(data.substring(2), (byte) data.charAt(1));
                break;

            case 66: //Basic ('B')
                this.basicHandler.handle(data.substring(2), (byte) data.charAt(1));
                break;

            case 68: //Dialog ('D')
                this.dialogHandler.handle(data.substring(2), (byte) data.charAt(1));
                break;

            case 71: //Game ('G')
                this.gameHandler.handle(data.substring(2), (byte) data.charAt(1));
                break;

            case 79: //Item ('O')
                this.itemHandler.handle(data.substring(2), (byte) data.charAt(1));
                break;

            case 101: //Environment ('e')
                this.environmentHandler.handle(data.substring(2), (byte) data.charAt(1));
                break;

            default:
                log.error("not implemented packet {}", data);
        }

    }
}
