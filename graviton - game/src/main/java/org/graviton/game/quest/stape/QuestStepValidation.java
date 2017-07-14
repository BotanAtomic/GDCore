package org.graviton.game.quest.stape;

import org.graviton.game.quest.Quest;

/**
 * Created by Botan on 13/07/17. 01:28
 */
public interface QuestStepValidation {

    QuestStepValidationType type();

    boolean validate(Quest quest, QuestStep questStep, String...arguments);

}
