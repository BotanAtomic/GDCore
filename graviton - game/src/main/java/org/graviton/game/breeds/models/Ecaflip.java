package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import java.util.HashMap;
import java.util.Map;

import static org.graviton.converter.Converters.*;

/**
 * Created by Botan on 05/11/2016 : 23:05
 */
public class Ecaflip extends AbstractBreed {
    private static final Map<Short, Short> spells = new HashMap<Short, Short>() {{
        put((short) 3, (short) 109);
        put((short) 6, (short) 113);
        put((short) 9, (short) 111);
        put((short) 13, (short) 104);
        put((short) 17, (short) 119);
        put((short) 21, (short) 101);
        put((short) 26, (short) 107);
        put((short) 31, (short) 116);
        put((short) 36, (short) 106);
        put((short) 42, (short) 117);
        put((short) 48, (short) 108);
        put((short) 54, (short) 115);
        put((short) 60, (short) 118);
        put((short) 70, (short) 110);
        put((short) 80, (short) 112);
        put((short) 90, (short) 114);
        put((short) 100, (short) 120);
        put((short) 200, (short) 1905);
    }};
    private static short[] startSpells = {102, 103, 105};

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
                return COSTM.apply(value);

            case 15:
            case 13: //Chance
                return COSTL.apply(value);

            case 14: //Agility
                return COSTM1.apply(value);
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
