package org.graviton.game.statistics.common;

import org.graviton.game.statistics.BaseCharacteristic;
import org.graviton.game.statistics.Life;

import java.util.HashMap;

/**
 * Created by Botan on 11/12/2016. 11:39
 */
public abstract class Statistics extends HashMap<CharacteristicType, Characteristic> {

    public abstract Statistics copy();

    public abstract Life getLife();

    public void clearBuffs() {
        super.values().forEach(Characteristic::clearBuff);
    }

    protected void initialize() {
        for (CharacteristicType type : CharacteristicType.values())
            put(type, new BaseCharacteristic((short) 0));
    }

}
