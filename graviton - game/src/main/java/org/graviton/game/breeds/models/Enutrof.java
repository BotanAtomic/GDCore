package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import java.util.HashMap;
import java.util.Map;

import static org.graviton.converter.Converters.*;
import static org.graviton.utils.Utils.range;

/**
 * Created by Botan on 05/11/2016 : 23:05
 */
public class Enutrof extends AbstractBreed {
    private static final Map<Short, Short> spells = new HashMap<Short, Short>() {{
        put((short) 3, (short) 49);
        put((short) 6, (short) 42);
        put((short) 9, (short) 47);
        put((short) 13, (short) 48);
        put((short) 17, (short) 45);
        put((short) 21, (short) 53);
        put((short) 26, (short) 46);
        put((short) 31, (short) 52);
        put((short) 36, (short) 44);
        put((short) 42, (short) 50);
        put((short) 48, (short) 54);
        put((short) 54, (short) 55);
        put((short) 60, (short) 56);
        put((short) 70, (short) 58);
        put((short) 80, (short) 59);
        put((short) 90, (short) 57);
        put((short) 100, (short) 60);
        put((short) 200, (short) 1903);
    }};
    private static short[] startSpells = {51, 43, 41};

    @Override
    public byte id() {
        return 3;
    }

    @Override
    public int astrubMap() {
        return 7442;
    }

    @Override
    public short astrubCell() {
        return 182;
    }

    @Override
    public int incarnamMap() {
        return 10299;
    }

    @Override
    public short incarnamCell() {
        return 271;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        switch (characteristicId) {
            case 10: //Strength
                return COSTM0.apply(value);

            case 13: //Chance
                return COSTM.apply(value);

            case 14: //Agility
                return COSTL.apply(value);

            case 15: //Intelligence
                if (range(value, 0, 20)) return (byte) 1;
                else if (range(value, 21, 60)) return (byte) 2;
                else if (range(value, 61, 100)) return (byte) 3;
                else if (range(value, 101, 140)) return (byte) 4;
                else return (byte) 5;
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
