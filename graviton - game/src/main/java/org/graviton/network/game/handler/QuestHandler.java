package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.QuestPacketFormatter;

/**
 * Created by Botan on 13/07/17. 16:27
 */

@Slf4j
public class QuestHandler {
    private final GameClient client;

    public QuestHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, char subHeader) {
        switch (subHeader) {
            case 'L':
                client.send(QuestPacketFormatter.personalQuestMessage(client.getPlayer()));
                break;
            case 'S':
                select(Short.parseShort(data));
                break;
            default:
                log.error("not implemented quest packet '{}'", subHeader);
        }
    }

    private void select(short quest) {
        client.send(QuestPacketFormatter.selectionMessage(client.getPlayer().getQuest(quest), client.getPlayer()));
    }
}
