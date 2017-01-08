package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import java.util.HashMap;
import java.util.Map;

import static org.graviton.converter.Converters.*;


/**
 * Created by Botan on 05/11/2016 : 23:05
 */
public class Eniripsa extends AbstractBreed {
    private static final Map<Short, Short> spells = new HashMap<Short, Short>() {{
        put((short) 3, (short) 124);
        put((short) 6, (short) 122);
        put((short) 9, (short) 126);
        put((short) 13, (short) 127);
        put((short) 17, (short) 123);
        put((short) 21, (short) 130);
        put((short) 26, (short) 131);
        put((short) 31, (short) 132);
        put((short) 36, (short) 133);
        put((short) 42, (short) 134);
        put((short) 48, (short) 135);
        put((short) 54, (short) 129);
        put((short) 60, (short) 136);
        put((short) 70, (short) 137);
        put((short) 80, (short) 138);
        put((short) 90, (short) 139);
        put((short) 100, (short) 140);
        put((short) 200, (short) 1907);
    }};
    private static short[] startSpells = {125, 128, 121};

    @Override
    public byte id() {
        return 7;
    }

    @Override
    public int astrubMap() {
        return 7316;
    }

    @Override
    public short astrubCell() {
        return 222;
    }

    @Override
    public int incarnamMap() {
        return 10283;
    }

    @Override
    public short incarnamCell() {
        return 299;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        switch (characteristicId) {
            case 10: //Strength
                return COSTV.apply(value);

            case 13: //Chance
            case 14: //Agility
                return COSTL.apply(value);

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
