package org.graviton.game.quest.stape.type;

import org.graviton.game.quest.Quest;
import org.graviton.game.quest.stape.QuestStep;
import org.graviton.game.quest.stape.QuestStepValidation;
import org.graviton.game.quest.stape.QuestStepValidationType;

/**
 * Created by Botan on 13/07/17. 02:35
 */
public class MonsterStep implements QuestStepValidation {

    @Override
    public QuestStepValidationType type() {
        return QuestStepValidationType.MONSTER;
    }

    @Override
    public boolean validate(Quest quest, QuestStep questStep, String... arguments) {
        short monsterId = Short.parseShort(arguments[0]);


        if (questStep.getMonster().getKey() == monsterId) {
            quest.incrementMonsterKilled(monsterId);
            if (quest.getMonstersKilled().get(monsterId) >= questStep.getMonster().getValue()) {
                quest.getMonstersKilled().remove(monsterId);
                return true;
            }
        }

        return false;
    }
}
