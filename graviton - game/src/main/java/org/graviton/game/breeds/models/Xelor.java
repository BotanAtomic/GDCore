package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

/**
 * Created by Botan on 05/11/2016 : 23:05
 */
public class Xelor extends AbstractBreed {
    @Override
    public byte id() {
        return 5;
    }

    @Override
    public int astrubMap() {
        return 10289;
    }

    @Override
    public short astrubCell() {
        return 236;
    }

    @Override
    public int incarnamMap() {
        return 10298;
    }

    @Override
    public short incarnamCell() {
        return 300;
    }
}
