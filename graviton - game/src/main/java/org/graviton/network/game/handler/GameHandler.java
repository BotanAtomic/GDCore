package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 22/11/2016 : 22:10
 */
@Slf4j
public class GameHandler {

    private final GameClient client;

    public GameHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, byte subHeader) { // 'G'
        switch (subHeader) {
            case 65: // 'A'
                client.createAction(Short.parseShort(data.substring(0, 3)), data.substring(3));
                break;

            case 67: // 'C'
                client.createGame();
                break;

            case 73: // 'I'
                client.sendGameInformation();
                break;

            case 75: //'K'
                client.finishAction(data);
                break;

            default:
                log.error("not implemented game packet '{}'", (char) subHeader);
        }

    }

}
