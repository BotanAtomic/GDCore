package org.graviton.game.action.npc;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.game.client.player.Player;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.network.game.protocol.QuestPacketFormatter;

/**
 * Created by Botan on 08/12/2016. 22:31
 */

@GameAction(id = 984)
public class FinishQuest implements Action {

    @Override
    public void apply(GameClient client, Object data) {
        String[] arguments = ((String) data).split(",");
        long experience = Long.parseLong(arguments[0]);
        short gameMap = Short.parseShort(arguments[1]);
        short questId = Short.parseShort(arguments[2]);

        Player player = client.getPlayer();

        if (player.getMap().getId() == gameMap)
            if (player.getQuest(questId) == null) {
                player.obtainQuest(client.getEntityFactory().getQuests().get(questId));
                player.getQuest(questId).setFinish(true);
                if (experience > 0) {
                    player.addExperience(experience);
                    player.send(MessageFormatter.experienceWinMessage(experience));
                }
                player.send(QuestPacketFormatter.finishQuestStaticMessage(questId));
            }

        client.getBaseHandler().getDialogHandler().leaveDialog();
    }

    @Override
    public void finish() {

    }

}
