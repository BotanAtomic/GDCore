package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import static org.graviton.game.breeds.Converter.*;

/**
 * Created by Botan on 05/11/2016 : 23:05
 */
public class Osamodas extends AbstractBreed {
    @Override
    public byte id() {
        return 2;
    }

    @Override
    public int astrubMap() {
        return 7545;
    }

    @Override
    public short astrubCell() {
        return 340;
    }

    @Override
    public int incarnamMap() {
        return 10284;
    }

    @Override
    public short incarnamCell() {
        return 372;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        switch (characteristicId) {
            case 10: //Strength
                return VERY_LITTLE.apply(value);

            case 13: //Chance
                return MEDIUM.apply(value);

            case 14: //Agility
                return LITTLE.apply(value);

            case 15: //Intelligence
                return MEDIUM.apply(value);
        }
        return 1;
    }
}
