package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import static org.graviton.game.breeds.Converter.LITTLE;
import static org.graviton.game.breeds.Converter.MEDIUM;

/**
 * Created by Botan on 05/11/2016 : 23:04
 */
public class Iop extends AbstractBreed {
    @Override
    public byte id() {
        return 8;
    }

    @Override
    public int astrubMap() {
        return 7427;
    }

    @Override
    public short astrubCell() {
        return 267;
    }

    @Override
    public int incarnamMap() {
        return 10294;
    }

    @Override
    public short incarnamCell() {
        return 280;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        switch (characteristicId) {
            case 10: //Strength
                return MEDIUM.apply(value);

            case 13: //Chance
                return LITTLE.apply(value);

            case 14: //Agility
                return LITTLE.apply(value);

            case 15: //Intelligence
                return LITTLE.apply(value);
        }
        return 1;
    }
}
