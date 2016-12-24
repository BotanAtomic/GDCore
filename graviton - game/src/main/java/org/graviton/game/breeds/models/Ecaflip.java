package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import static org.graviton.game.breeds.Converter.*;

/**
 * Created by Botan on 05/11/2016 : 23:05
 */
public class Ecaflip extends AbstractBreed {
    @Override
    public byte id() {
        return 6;
    }

    @Override
    public int astrubMap() {
        return 7446;
    }

    @Override
    public short astrubCell() {
        return 313;
    }

    @Override
    public int incarnamMap() {
        return 10276;
    }

    @Override
    public short incarnamCell() {
        return 296;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {

        switch (characteristicId) {
            case 10: //Strength
                return MEDIUM.apply(value);

            case 15:
            case 13: //Chance
                return LITTLE.apply(value);

            case 14: //Agility
                return MEDIUM1.apply(value);
        }

        return 1;
    }

}
