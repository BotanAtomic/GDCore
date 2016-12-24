package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import static org.graviton.game.breeds.Converter.*;

/**
 * Created by Botan on 05/11/2016 : 23:00
 */
public class Cra extends AbstractBreed {
    @Override
    public byte id() {
        return 9;
    }


    @Override
    public int astrubMap() {
        return 7378;
    }

    @Override
    public short astrubCell() {
        return 310;
    }

    @Override
    public int incarnamMap() {
        return 10292;
    }

    @Override
    public short incarnamCell() {
        return 284;
    }

    @Override
    public byte boostCost(byte statistics, short value) {
        switch (statistics) {
            case 15:
            case 10: //Strength
                return MEDIUM0.apply(value);

            case 13: //Chance
                return LITTLE.apply(value);

            case 14: //Agility
                return MEDIUM1.apply(value);
        }
        return 1;
    }
}
