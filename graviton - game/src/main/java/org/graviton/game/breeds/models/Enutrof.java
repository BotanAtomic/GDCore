package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import static org.graviton.game.breeds.Converter.*;

/**
 * Created by Botan on 05/11/2016 : 23:05
 */
public class Enutrof extends AbstractBreed {
    @Override
    public byte id() {
        return 3;
    }

    @Override
    public int astrubMap() {
        return 7442;
    }

    @Override
    public short astrubCell() {
        return 182;
    }

    @Override
    public int incarnamMap() {
        return 10299;
    }

    @Override
    public short incarnamCell() {
        return 271;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        switch (characteristicId) {
            case 10: //Strength
                return MEDIUM0.apply(value);

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
