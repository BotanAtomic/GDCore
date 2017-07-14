package org.graviton.game.quest.stape.type;

import org.graviton.game.quest.Quest;
import org.graviton.game.quest.stape.QuestStep;
import org.graviton.game.quest.stape.QuestStepValidation;
import org.graviton.game.quest.stape.QuestStepValidationType;

/**
 * Created by Botan on 13/07/17. 02:35
 */
public class SpeakStep implements QuestStepValidation {

    @Override
    public QuestStepValidationType type() {
        return QuestStepValidationType.SPEAK;
    }

    @Override
    public boolean validate(Quest quest, QuestStep questStep, String... arguments) {
        return questStep.getNpc() == Short.parseShort(arguments[0]);
    }
}
