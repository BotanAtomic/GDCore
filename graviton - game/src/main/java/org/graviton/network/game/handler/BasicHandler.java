package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 03/12/2016. 16:21
 */

@Slf4j
public class BasicHandler {
    private final GameClient client;

    public BasicHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, byte subHeader) { // 'B'
        switch (subHeader) {
            case 77: // 'M'
                this.client.speak(data.split("\\|"));
                break;

            case 97: // 'a'
                this.client.changePlayerMapByPosition(data);
                break;

            default:
                log.error("not implemented basic packet '{}'", (char) subHeader);
        }

    }
}
