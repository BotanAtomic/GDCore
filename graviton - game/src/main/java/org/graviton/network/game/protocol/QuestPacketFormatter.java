package org.graviton.network.game.protocol;

import org.graviton.game.client.player.Player;
import org.graviton.game.creature.npc.NpcTemplate;
import org.graviton.game.quest.Quest;

/**
 * Created by Botan on 13/07/17. 02:06
 */
public class QuestPacketFormatter {


    public static String newQuestStaticMessage(short id) {
        return MessageFormatter.customMessage("054;") + id;
    }

    public static String updateQuestStaticMessage(short id) {
        return MessageFormatter.customMessage("055;") + id;
    }

    public static String finishQuestStaticMessage(short id) {
        return MessageFormatter.customMessage("056;") + id;
    }


    public static String personalQuestMessage(Player player) {
        StringBuilder builder = new StringBuilder("QL+");
        player.getQuests().forEach(quest -> builder.append(quest.getQuest().getId()).append(";").append(quest.isFinish() ? 1 : 0).append("|"));
        return player.getQuests().isEmpty() ? builder.toString() : builder.substring(0, builder.length() - 1);
    }

    public static String selectionMessage(Quest quest, Player player) {
        short currentGoal = quest.currentGoal();
        short previousGoal = quest.previousGoal();
        short nextGoal = quest.nextGoal();

        System.err.println(currentGoal + "/" + previousGoal + "/" + nextGoal);

        StringBuilder builder = new StringBuilder();
        builder.append(quest.getQuest().getId()).append("|").append((currentGoal > 0) ? currentGoal : "").append("|");

        final StringBuilder stepBuilder = new StringBuilder();

        quest.getQuest().getSteps().forEach((stepId, step) -> {
            System.err.println("step goal id = " + step.getQuestGoal().getId());
            if (step.getQuestGoal().getId() == currentGoal && quest.respectCondition(step))
                stepBuilder.append(step.getId()).append(",").append(quest.getValidateSteps().containsKey(step.getId()) ? 1 : 0).append(";");
        });

        builder.append(stepBuilder.length() > 0 ? stepBuilder.substring(0, stepBuilder.length() - 1) : stepBuilder).append("|");
        builder.append((previousGoal > 0) ? previousGoal : "").append("|");
        builder.append((nextGoal > 0) ? nextGoal : "");

        if (quest.getQuest().getNpc() != 0) {
            builder.append("|");
            NpcTemplate npc = player.getAccount().getClient().getEntityFactory().getNpcTemplate(quest.getQuest().getNpc());
            builder.append(npc.getInitialQuestion(player.getMap().getId())).append("|");
        }
        return "QS" + builder;
    }

}
