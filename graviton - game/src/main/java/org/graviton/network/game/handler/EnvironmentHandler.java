package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 07/12/2016. 14:26
 */

@Slf4j
public class EnvironmentHandler {
    private final GameClient client;

    public EnvironmentHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, byte subHeader) { // 'e'
        switch (subHeader) {
            case 68: // 'D'
                client.changeOrientation(Byte.parseByte(data));
                break;


            default:
                log.error("not implemented environment packet '{}'", (char) subHeader);
        }
    }
}
