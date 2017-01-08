package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import java.util.HashMap;
import java.util.Map;

import static org.graviton.utils.Utils.range;

/**
 * Created by Botan on 05/11/2016 : 23:08
 */
public class Pandawa extends AbstractBreed {
    private static final Map<Short, Short> spells = new HashMap<Short, Short>() {{
        put((short) 3, (short) 689);
        put((short) 6, (short) 690);
        put((short) 9, (short) 691);
        put((short) 13, (short) 688);
        put((short) 17, (short) 693);
        put((short) 21, (short) 694);
        put((short) 26, (short) 695);
        put((short) 31, (short) 696);
        put((short) 36, (short) 697);
        put((short) 42, (short) 698);
        put((short) 48, (short) 699);
        put((short) 54, (short) 700);
        put((short) 60, (short) 701);
        put((short) 70, (short) 702);
        put((short) 80, (short) 703);
        put((short) 90, (short) 704);
        put((short) 100, (short) 705);
        put((short) 200, (short) 1912);
    }};
    private static short[] startSpells = {686, 692, 687};

    @Override
    public byte id() {
        return 12;
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
        return 10289;
    }

    @Override
    public short incarnamCell() {
        return 236;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        if (range(value, 0, 50)) return 1;
        else if (range(value, 51, 200)) return 2;
        else return 3;
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
