package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import static org.graviton.game.breeds.Converter.*;

/**
 * Created by Botan on 05/11/2016 : 23:01
 */
public class Sram extends AbstractBreed {
    @Override
    public byte id() {
        return 4;
    }

    @Override
    public int astrubMap() {
        return 7392;
    }

    @Override
    public short astrubCell() {
        return 313;
    }

    @Override
    public int incarnamMap() {
        return 10285;
    }

    @Override
    public short incarnamCell() {
        return 263;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        switch (characteristicId) {
            case 14:
            case 10: //Strength
                return MEDIUM.apply(value);

            case 13: //Chance
                return LITTLE.apply(value);

            case 15: //Intelligence
                return VERY_LITTLE.apply(value);
        }

        return 1;
    }
}
