package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import java.util.HashMap;
import java.util.Map;

import static org.graviton.converter.Converters.*;

/**
 * Created by Botan on 05/11/2016 : 23:00
 */
public class Cra extends AbstractBreed {
    private static final Map<Short, Short> spells = new HashMap<Short, Short>() {{
        put((short) 3, (short) 163);
        put((short) 6, (short) 165);
        put((short) 9, (short) 172);
        put((short) 13, (short) 167);
        put((short) 17, (short) 168);
        put((short) 21, (short) 162);
        put((short) 26, (short) 170);
        put((short) 31, (short) 171);
        put((short) 36, (short) 166);
        put((short) 42, (short) 173);
        put((short) 48, (short) 174);
        put((short) 54, (short) 176);
        put((short) 60, (short) 175);
        put((short) 70, (short) 178);
        put((short) 80, (short) 177);
        put((short) 90, (short) 179);
        put((short) 100, (short) 180);
        put((short) 200, (short) 1909);
    }};
    private static short[] startSpells = {161, 164, 169};

    @Override
    public byte id() {
        return 9;
    }

    @Override
    public int astrubMap() {
        return 7378;
    }

    @Override
    public short astrubCell() {
        return 310;
    }

    @Override
    public int incarnamMap() {
        return 10292;
    }

    @Override
    public short incarnamCell() {
        return 284;
    }

    @Override
    public byte boostCost(byte statistics, short value) {
        switch (statistics) {
            case 15:
            case 10: //Strength
                return COSTM0.apply(value);

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
