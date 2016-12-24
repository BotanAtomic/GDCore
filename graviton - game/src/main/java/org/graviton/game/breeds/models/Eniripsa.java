package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import static org.graviton.game.breeds.Converter.*;

/**
 * Created by Botan on 05/11/2016 : 23:05
 */
public class Eniripsa extends AbstractBreed {
    @Override
    public byte id() {
        return 7;
    }

    @Override
    public int astrubMap() {
        return 7316;
    }

    @Override
    public short astrubCell() {
        return 222;
    }

    @Override
    public int incarnamMap() {
        return 10283;
    }

    @Override
    public short incarnamCell() {
        return 299;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        switch (characteristicId) {
            case 10: //Strength
                return VERY_LITTLE.apply(value);

            case 13: //Chance
            case 14: //Agility
                return LITTLE.apply(value);

            case 15: //Intelligence
                return MEDIUM.apply(value);
        }
        return 1;
    }
}
