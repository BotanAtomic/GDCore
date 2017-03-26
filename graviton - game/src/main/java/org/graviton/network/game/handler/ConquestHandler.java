package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.ConquestPacketFormatter;

/**
 * Created by Botan on 12/03/2017. 13:37
 */

@Slf4j
public class ConquestHandler {
    private final GameClient client;

    public ConquestHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, char subHeader) {
        switch (subHeader) {
            case 'W':
                parseWorldInformation(data.charAt(0));
                break;

            case 'B':
                parseAlignmentInformation();
                break;

            default:
                log.error("not implemented conquest packet '{}'", subHeader);
        }
    }

    private void parseAlignmentInformation() {

    }

    private void parseWorldInformation(char data) {
        switch (data) {
            case 'J' :
                client.send(ConquestPacketFormatter.prismPositionMessage());
                break;
        }
    }
}
