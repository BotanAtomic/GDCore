package org.graviton.game.quest.stape;

import org.graviton.game.quest.Quest;
import org.graviton.game.quest.stape.type.FollowerStep;
import org.graviton.game.quest.stape.type.ItemStep;
import org.graviton.game.quest.stape.type.MonsterStep;
import org.graviton.game.quest.stape.type.SpeakStep;
import org.graviton.utils.Utils;

import java.util.stream.Stream;


/**
 * Created by Botan on 13/07/17. 02:37
 */
public enum QuestStepValidationType {
    FOLLOWER(FollowerStep.class, (byte) 10),
    ITEM(ItemStep.class, (byte) 3),
    MONSTER(MonsterStep.class, (byte) 6),
    SPEAK(SpeakStep.class, new byte[]{0, 1, 9}),
    BASIC(null);


    static QuestStepValidation SIMPLE = new QuestStepValidation() {
        @Override public QuestStepValidationType type() {
            return BASIC;
        }

        public boolean validate(Quest quest, QuestStep questStep, String... arguments) {
            return true;
        }
    };

    final byte[] id;
    final Class<? extends QuestStepValidation> type;

    QuestStepValidationType(Class<? extends QuestStepValidation> type, byte... id) {
        this.id = id;
        this.type = type;
    }

    public static QuestStepValidationType get(byte type) {
        return Stream.of(values()).filter(questStepValidation -> Utils.containsInByteArray(questStepValidation.id, type)).findAny().orElse(BASIC);
    }

    public QuestStepValidation create() {
        if(type == null) return SIMPLE;
        try {
            return type.newInstance();
        } catch (Exception ignored) {
            return null;
        }
    }

}
