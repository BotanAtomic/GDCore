package org.graviton.game.statistics;

import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.common.Statistics;

/**
 * Created by Botan on 15/01/2017. 14:17
 */
public class CriticalRate extends Characteristic {
    private final Statistics statistics;

    public CriticalRate(Statistics statistics) {
        super((short) 0);
        this.statistics = statistics;
    }

    @Override
    public int total() {
        short baseTotal = (short) (super.base + super.equipment + super.context + super.gift);
        int agility = statistics.get(CharacteristicType.Agility).total();

        if (agility < 8)
            return baseTotal;

        return (short) (baseTotal * 2.9901 / Math.log(agility + 12));
    }
}