package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import static org.graviton.game.breeds.Converter.*;

/**
 * Created by Botan on 05/11/2016 : 23:01
 */
public class Feca extends AbstractBreed {
    @Override
    public byte id() {
        return 1;
    }

    @Override
    public int astrubMap() {
        return 7398;
    }

    @Override
    public short astrubCell() {
        return 299;
    }

    @Override
    public int incarnamMap() {
        return 323;
    }

    @Override
    public short incarnamCell() {
        return 10300;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        switch (characteristicId) {
            case 10: //Strength
                return VERY_LITTLE.apply(value);

            case 13: //Chance
                return LITTLE.apply(value);

            case 14: //Agility
                return LITTLE.apply(value);

            case 15: //Intelligence
                return MEDIUM.apply(value);
        }
        return 1;
    }
}
