package org.graviton.game.intelligence.common;

import org.graviton.game.fight.Fighter;
import org.graviton.game.intelligence.ArtificialIntelligence;
import org.graviton.game.intelligence.artificial.AttackIntelligence;
import org.graviton.game.intelligence.artificial.PassIntelligence;
import org.graviton.game.intelligence.artificial.TofuIntelligence;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Botan on 18/01/2017. 23:37
 */
public enum IntelligenceType {
    PASS((byte) 0, PassIntelligence.class),
    IA1((byte) 1, AttackIntelligence.class),
    IA12((byte) 7, TofuIntelligence.class);

    private final byte value;
    private final Class<? extends ArtificialIntelligence> intelligenceClass;

    <V extends ArtificialIntelligence> IntelligenceType(byte value, Class<V> intelligenceClass) {
        this.value = value;
        this.intelligenceClass = intelligenceClass;
    }

    public static IntelligenceType get(byte value) {
        for (IntelligenceType intelligenceType : values())
            if (intelligenceType.value == value)
                return intelligenceType;
        return PASS;
    }

    public ArtificialIntelligence create(Fighter fighter) {
        try {
            return intelligenceClass.getDeclaredConstructor(Fighter.class).newInstance(fighter);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return new PassIntelligence(fighter);
        }
    }
}
