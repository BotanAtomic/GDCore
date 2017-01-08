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
    private final GameClient client;
    private final AccountHandler accountHandler;
    private final GameHandler gameHandler;
    private final BasicHandler basicHandler;
    private final ItemHandler itemHandler;
    private final EnvironmentHandler environmentHandler;
    private final DialogHandler dialogHandler;
    private final SpellHandler spellHandler;

    public MessageHandler(GameClient gameClient) {
        this.client = gameClient;
        this.accountHandler = new AccountHandler(gameClient);
        this.gameHandler = new GameHandler(gameClient);
        this.basicHandler = new BasicHandler(gameClient);
        this.itemHandler = new ItemHandler(gameClient);
        this.environmentHandler = new EnvironmentHandler(gameClient);
        this.dialogHandler = new DialogHandler(gameClient);
        this.spellHandler = new SpellHandler(gameClient);
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

            case 102: //Fight 'f'
            case 71: //Game ('G')
                this.gameHandler.handle(data.substring(2), (byte) data.charAt(1));
                break;

            case 79: //Item ('O')
                this.itemHandler.handle(data.substring(2), (byte) data.charAt(1));
                break;

            case 83: //Spell ('S')
                this.spellHandler.handle(data.substring(2), (byte) data.charAt(1));
                break;

            case 101: //Environment ('e')
                this.environmentHandler.handle(data.substring(2), (byte) data.charAt(1));
                break;

            default:
                log.error("not implemented packet {}", data);
        }

    }
}
