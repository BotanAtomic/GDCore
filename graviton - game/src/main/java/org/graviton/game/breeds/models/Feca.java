package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Botan on 05/11/2016 : 23:01
 */
public class Feca extends AbstractBreed {
    private static final Map<Short, Short> spells = new HashMap<Short, Short>() {{
        put((short) 3, (short) 4);
        put((short) 6, (short) 2);
        put((short) 9, (short) 1);
        put((short) 13, (short) 9);
        put((short) 17, (short) 18);
        put((short) 21, (short) 20);
        put((short) 26, (short) 14);
        put((short) 31, (short) 19);
        put((short) 36, (short) 5);
        put((short) 42, (short) 16);
        put((short) 48, (short) 8);
        put((short) 54, (short) 12);
        put((short) 60, (short) 11);
        put((short) 70, (short) 10);
        put((short) 80, (short) 7);
        put((short) 90, (short) 15);
        put((short) 100, (short) 13);
        put((short) 200, (short) 1901);
    }};
    private static short[] startSpells = {3, 6, 17};

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
        return 10300;
    }

    @Override
    public short incarnamCell() {
        return 323;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        return new Eniripsa().boostCost(characteristicId, value); //same...
    }

    @Override
    public short[] getStartSpells() {
        return startSpells;
    }

    @Override
    public short getSpell(short level) {
        if (!spells.containsKey(level))
            return 0;
        return spells.get(level);
    }
}
