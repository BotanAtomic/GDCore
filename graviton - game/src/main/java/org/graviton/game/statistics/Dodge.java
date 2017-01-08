package org.graviton.game.statistics;

import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.type.PlayerStatistics;

/**
 * Created by Botan on 29/12/2016. 15:54
 */
public class Dodge extends Characteristic {
    private final PlayerStatistics playerStatistics;

    public Dodge(PlayerStatistics statistics) {
        super((short) 0);
        this.playerStatistics = statistics;
    }

    @Override
    public short total() {
        return (short) (super.base + super.equipment + super.context + super.gift + (playerStatistics.get(CharacteristicType.Wisdom).total() / 4));
    }
}
