package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import java.util.HashMap;
import java.util.Map;

import static org.graviton.converter.Converters.COSTL;
import static org.graviton.converter.Converters.COSTM;

/**
 * Created by Botan on 05/11/2016 : 23:05
 */
public class Sadida extends AbstractBreed {
    private static final Map<Short, Short> spells = new HashMap<Short, Short>() {{
        put((short) 3, (short) 198);
        put((short) 6, (short) 195);
        put((short) 9, (short) 182);
        put((short) 13, (short) 192);
        put((short) 17, (short) 197);
        put((short) 21, (short) 189);
        put((short) 26, (short) 181);
        put((short) 31, (short) 199);
        put((short) 36, (short) 191);
        put((short) 42, (short) 186);
        put((short) 48, (short) 196);
        put((short) 54, (short) 190);
        put((short) 60, (short) 194);
        put((short) 70, (short) 185);
        put((short) 80, (short) 184);
        put((short) 90, (short) 188);
        put((short) 100, (short) 187);
        put((short) 200, (short) 1910);
    }};
    private static short[] startSpells = {183, 200, 193};

    @Override
    public byte id() {
        return 10;
    }

    @Override
    public int astrubMap() {
        return 7395;
    }

    @Override
    public short astrubCell() {
        return 371;
    }

    @Override
    public int incarnamMap() {
        return 10279;
    }

    @Override
    public short incarnamCell() {
        return 254;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        switch (characteristicId) {
            case 10: //Strength
            case 13: //Chance
            case 15: // Intelligence
                return COSTM.apply(value);

            case 14: //Agility
                return COSTL.apply(value);

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
