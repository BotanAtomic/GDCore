package org.graviton.game.statistics.common;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by Botan on 11/12/2016. 11:39
 */
public abstract class Statistics {
    private final Map<CharacteristicType, Characteristic> characteristics = Maps.newHashMap();

    public void put(CharacteristicType type, Characteristic characteristic) {
        this.characteristics.put(type, characteristic);
    }

    public Characteristic get(CharacteristicType type) {
        return this.characteristics.get(type);
    }
}
