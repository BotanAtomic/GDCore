package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import java.util.HashMap;
import java.util.Map;

import static org.graviton.converter.Converters.COSTH;

/**
 * Created by Botan on 05/11/2016 : 23:01
 */
public class Sacrieur extends AbstractBreed {
    private static final Map<Short, Short> spells = new HashMap<Short, Short>() {{
        put((short) 3, (short) 444);
        put((short) 6, (short) 449);
        put((short) 9, (short) 436);
        put((short) 13, (short) 437);
        put((short) 17, (short) 439);
        put((short) 21, (short) 431);
        put((short) 26, (short) 443);
        put((short) 31, (short) 440);
        put((short) 36, (short) 442);
        put((short) 42, (short) 441);
        put((short) 48, (short) 445);
        put((short) 54, (short) 438);
        put((short) 60, (short) 446);
        put((short) 70, (short) 447);
        put((short) 80, (short) 448);
        put((short) 90, (short) 435);
        put((short) 100, (short) 450);
        put((short) 200, (short) 1911);
    }};
    private static short[] startSpells = {432, 433, 434};

    @Override
    public byte id() {
        return 11;
    }

    @Override
    public int astrubMap() {
        return 7336;
    }

    @Override
    public short astrubCell() {
        return 197;
    }

    @Override
    public int incarnamMap() {
        return 10296;
    }

    @Override
    public short incarnamCell() {
        return 243;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        return COSTH.apply(value);
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
