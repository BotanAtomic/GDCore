package org.graviton.game.filter.type;

import org.graviton.game.client.player.Player;
import org.graviton.game.filter.Filter;
import org.graviton.game.filter.enums.FilterType;
import org.graviton.game.quest.stape.QuestStep;

/**
 * Created by Botan on 14/07/17. 14:28
 */
public class QuestStepFilter implements Filter {
    @Override
    public boolean check(Player player, FilterType filterType, String data) {
        QuestStep questStep = player.getEntityFactory().getQuestSteps().get(Short.parseShort(data));

        return questStep != null && player.activeQuests().stream().filter(quest -> quest.currentStep().getId() == questStep.getId()).count() > 0 && filterType == FilterType.EQUALS;

    }
}
