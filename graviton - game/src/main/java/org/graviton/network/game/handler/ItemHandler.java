package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 05/12/2016. 16:31
 */
@Slf4j
public class ItemHandler {
    private final GameClient client;

    public ItemHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, byte subHeader) { // 'O'
        switch (subHeader) {

            case 77: // 'M'
                this.client.objectMove(data.split("\\|"));
                break;

            default:
                log.error("not implemented item packet '{}'", (char) subHeader);
        }

    }
}
