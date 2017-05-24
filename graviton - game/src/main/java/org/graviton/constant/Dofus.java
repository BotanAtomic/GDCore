package org.graviton.constant;

import javafx.util.Pair;
import org.graviton.utils.Utils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Botan on 12/11/2016 : 15:02
 */
public class Dofus {
    public static final short MAX_OBJECT_IN_TRUNK = 4000;

    public static final byte MAX_STORE_PER_MAP = 5;

    public static final byte DISCONNECT_REMAINING_TURN = 20;

    public static final short MAX_ENERGY = 10000;

    public static final short MAX_LEVEL = 200;

    public static final short STARS_TIME = 120; //min

    public static final long TURN_TIME = 30000; //ms

    public static final byte MAX_SPELL_LEVEL = 6;

    public static final String GUILD_SPELLS = "462;0|461;0|460;0|459;0|458;0|457;0|456;0|455;0|454;0|453;0|452;0|451;0";

    public static final Pair<Byte, Integer> ANGEL_CONDITION = new Pair<>((byte) 10, 42);

    public static final Pair<Byte, Integer> DEVIL_CONDITION = new Pair<>((byte) 10, 95);

    public static final short WATER_OBJECT = 311;

    public static short knightLevel(short level) {
        if (level <= 50)
            return 50;
        if (Utils.range(level, 50, 80))
            return 80;
        if (Utils.range(level, 80, 110))
            return 110;
        if (Utils.range(level, 110, 140))
            return 140;
        return 170;
    }

    public static class Monster {
        public static int KNIGHT = 394;
    }

}
