package org.graviton.game.breeds.models;

import org.graviton.game.breeds.AbstractBreed;

import java.util.HashMap;
import java.util.Map;

import static org.graviton.converter.Converters.COSTL;
import static org.graviton.converter.Converters.COSTM;


/**
 * Created by Botan on 05/11/2016 : 23:04
 */
public class Iop extends AbstractBreed {
    private static final Map<Short, Short> spells = new HashMap<Short, Short>() {{
        put((short) 3, (short) 144);
        put((short) 6, (short) 145);
        put((short) 9, (short) 146);
        put((short) 13, (short) 147);
        put((short) 17, (short) 148);
        put((short) 21, (short) 154);
        put((short) 26, (short) 150);
        put((short) 31, (short) 151);
        put((short) 36, (short) 155);
        put((short) 42, (short) 152);
        put((short) 48, (short) 153);
        put((short) 54, (short) 149);
        put((short) 60, (short) 156);
        put((short) 70, (short) 157);
        put((short) 80, (short) 158);
        put((short) 90, (short) 160);
        put((short) 100, (short) 159);
        put((short) 200, (short) 1908);
    }};
    private static short[] startSpells = {143, 141, 142};

    @Override
    public byte id() {
        return 8;
    }

    @Override
    public int astrubMap() {
        return 7427;
    }

    @Override
    public short astrubCell() {
        return 267;
    }

    @Override
    public int incarnamMap() {
        return 10294;
    }

    @Override
    public short incarnamCell() {
        return 280;
    }

    @Override
    public byte boostCost(byte characteristicId, short value) {
        switch (characteristicId) {
            case 10: //Strength
                return COSTM.apply(value);

            case 13: //Chance
            case 14: //Agility
            case 15: //Intelligence
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
