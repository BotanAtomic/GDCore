package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import static org.graviton.game.breeds.Converter.LITTLE;
import static org.graviton.game.breeds.Converter.MEDIUM;

/**
 * Created by Botan on 05/11/2016 : 23:05
 */
public class Sadida extends AbstractBreed {
    @Override
    public byte id() {
        return 10;
    }

    @Override
    public int astrubMap() {
        return 7395;
    }

    @Override
    public short astrubCell() {
        return 371;
    }

    @Override
    public int incarnamMap() {
        return 10279;
    }

    @Override
    public short incarnamCell() {
        return 254;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        switch (characteristicId) {
            case 10: //Strength
            case 13: //Chance
            case 15: // Intelligence
                return MEDIUM.apply(value);

            case 14: //Agility
                return LITTLE.apply(value);

        }
        return 1;
    }
}
