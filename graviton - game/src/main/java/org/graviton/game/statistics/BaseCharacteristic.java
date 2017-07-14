package org.graviton.game.statistics;

import org.graviton.game.statistics.common.Characteristic;

/**
 * Created by Botan on 22/12/2016. 00:15
 */
public class BaseCharacteristic extends Characteristic {

    public BaseCharacteristic(int base) {
        super(base);
    }

    @Override
    public int total() {
        return (base + equipment + gift + context);
    }
}
