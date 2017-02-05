package org.graviton.game.statistics;

import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.common.Statistics;

/**
 * Created by Botan on 22/12/2016. 00:17
 */
public class Initiative extends Characteristic {
    private final Statistics statistics;

    public Initiative(Statistics statistics, short base) {
        super(base);
        this.statistics = statistics;
    }

    private int getMaxLife() {
        return statistics.getLife().getMaximum();
    }

    private int getCurrentLife() {
        return statistics.getLife().getCurrent();
    }

    private Characteristic get(CharacteristicType type) {
        return statistics.get(type);
    }

    @Override
    public int total() {
        double total = get(CharacteristicType.Strength).total() +
                get(CharacteristicType.Intelligence).total() +
                get(CharacteristicType.Chance).total() +
                get(CharacteristicType.Agility).total() +
                gift + context + equipment;
        return (short) (total * ((double) getCurrentLife() / (double) getMaxLife()));
    }
}


