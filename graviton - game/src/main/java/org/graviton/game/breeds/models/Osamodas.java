package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import java.util.HashMap;
import java.util.Map;

import static org.graviton.converter.Converters.*;

/**
 * Created by Botan on 05/11/2016 : 23:05
 */
public class Osamodas extends AbstractBreed {
    private static final Map<Short, Short> spells = new HashMap<Short, Short>() {{
        put((short) 3, (short) 26);
        put((short) 6, (short) 22);
        put((short) 9, (short) 35);
        put((short) 13, (short) 28);
        put((short) 17, (short) 37);
        put((short) 21, (short) 30);
        put((short) 26, (short) 27);
        put((short) 31, (short) 24);
        put((short) 36, (short) 33);
        put((short) 42, (short) 25);
        put((short) 48, (short) 38);
        put((short) 54, (short) 36);
        put((short) 60, (short) 32);
        put((short) 70, (short) 29);
        put((short) 80, (short) 39);
        put((short) 90, (short) 40);
        put((short) 100, (short) 31);
        put((short) 200, (short) 1902);
    }};
    private static short[] startSpells = {34, 21, 23};

    @Override
    public byte id() {
        return 2;
    }

    @Override
    public int astrubMap() {
        return 7545;
    }

    @Override
    public short astrubCell() {
        return 340;
    }

    @Override
    public int incarnamMap() {
        return 10284;
    }

    @Override
    public short incarnamCell() {
        return 372;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        switch (characteristicId) {
            case 10: //Strength
                return COSTV.apply(value);

            case 14: //Agility
                return COSTL.apply(value);

            case 13: //Chance
            case 15: //Intelligence
                return COSTM.apply(value);
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

