package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import java.util.HashMap;
import java.util.Map;

import static org.graviton.converter.Converters.*;


/**
 * Created by Botan on 05/11/2016 : 23:01
 */
public class Sram extends AbstractBreed {
    private static final Map<Short, Short> spells = new HashMap<Short, Short>() {{
        put((short) 3, (short) 66);
        put((short) 6, (short) 68);
        put((short) 9, (short) 63);
        put((short) 13, (short) 74);
        put((short) 17, (short) 64);
        put((short) 21, (short) 79);
        put((short) 26, (short) 78);
        put((short) 31, (short) 71);
        put((short) 36, (short) 62);
        put((short) 42, (short) 69);
        put((short) 48, (short) 77);
        put((short) 54, (short) 73);
        put((short) 60, (short) 67);
        put((short) 70, (short) 70);
        put((short) 80, (short) 75);
        put((short) 90, (short) 76);
        put((short) 100, (short) 80);
        put((short) 200, (short) 1904);
    }};
    private static short[] startSpells = {61, 72, 65};

    @Override
    public byte id() {
        return 4;
    }

    @Override
    public int astrubMap() {
        return 7392;
    }

    @Override
    public short astrubCell() {
        return 313;
    }

    @Override
    public int incarnamMap() {
        return 10285;
    }

    @Override
    public short incarnamCell() {
        return 263;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        switch (characteristicId) {
            case 14:
            case 10: //Strength
                return COSTM.apply(value);

            case 13: //Chance
                return COSTL.apply(value);

            case 15: //Intelligence
                return COSTV.apply(value);
        }

        return 1;
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

