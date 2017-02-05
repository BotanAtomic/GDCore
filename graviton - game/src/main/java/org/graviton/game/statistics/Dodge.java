package org.graviton.game.statistics;

import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.common.Statistics;

/**
 * Created by Botan on 29/12/2016. 15:54
 */
public class Dodge extends Characteristic {
    private final Statistics statistics;

    public Dodge(Statistics statistics) {
        super((short) 0);
        this.statistics = statistics;
    }

    @Override
    public int total() {
        return (short) (super.base + super.equipment + super.context + super.gift + (statistics.get(CharacteristicType.Wisdom).total() / 4));
    }
}
