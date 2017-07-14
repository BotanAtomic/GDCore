package org.graviton.game.quest.stape.type;

import org.graviton.game.quest.Quest;
import org.graviton.game.quest.stape.QuestStep;
import org.graviton.game.quest.stape.QuestStepValidation;
import org.graviton.game.quest.stape.QuestStepValidationType;

/**
 * Created by Botan on 13/07/17. 02:37
 */
public class FollowerStep implements QuestStepValidation {

    @Override
    public QuestStepValidationType type() {
        return QuestStepValidationType.FOLLOWER;
    }

    @Override
    public boolean validate(Quest quest, QuestStep questStep, String... arguments) {
        return false;
    }
}
