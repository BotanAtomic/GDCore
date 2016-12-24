package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import static org.graviton.game.breeds.Converter.HARD;

/**
 * Created by Botan on 05/11/2016 : 23:01
 */
public class Sacrieur extends AbstractBreed {
    @Override
    public byte id() {
        return 11;
    }

    @Override
    public int astrubMap() {
        return 7336;
    }

    @Override
    public short astrubCell() {
        return 197;
    }

    @Override
    public int incarnamMap() {
        return 10296;
    }

    @Override
    public short incarnamCell() {
        return 243;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        return HARD.apply(value);
    }
}
