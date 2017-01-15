package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Botan on 05/11/2016 : 23:05
 */
public class Xelor extends AbstractBreed {

    private static final Map<Short, Short> spells = new HashMap<Short, Short>() {{
        put((short) 3, (short) 84);
        put((short) 6, (short) 100);
        put((short) 9, (short) 92);
        put((short) 13, (short) 88);
        put((short) 17, (short) 93);
        put((short) 21, (short) 85);
        put((short) 26, (short) 96);
        put((short) 31, (short) 98);
        put((short) 36, (short) 86);
        put((short) 42, (short) 89);
        put((short) 48, (short) 90);
        put((short) 54, (short) 87);
        put((short) 60, (short) 94);
        put((short) 70, (short) 99);
        put((short) 80, (short) 95);
        put((short) 90, (short) 91);
        put((short) 100, (short) 97);
        put((short) 200, (short) 1909);
    }};

    private static short[] startSpells = {82, 81, 83};

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
