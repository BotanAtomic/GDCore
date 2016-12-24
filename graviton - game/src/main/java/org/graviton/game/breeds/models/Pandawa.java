package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import static org.graviton.utils.Utils.range;

/**
 * Created by Botan on 05/11/2016 : 23:08
 */
public class Pandawa extends AbstractBreed {
    @Override
    public byte id() {
        return 12;
    }

    @Override
    public int astrubMap() {
        return 10289;
    }

    @Override
    public short astrubCell() {
        return 236;
    }

    @Override
    public int incarnamMap() {
        return 10289;
    }

    @Override
    public short incarnamCell() {
        return 236;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        if (range(value, 0, 50)) return 1;
        else if (range(value, 51, 200)) return 2;
        else return 3;
    }
}
