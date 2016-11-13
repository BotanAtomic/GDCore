package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

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
}
