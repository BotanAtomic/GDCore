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
    public static final byte MAX_STORE_PER_MAP = 5;

    public static final short MAX_ENERGY = 10000;

    public static final short MAX_LEVEL = 200;

    public static final short STARS_TIME = 120; //min

    public static final long TURN_TIME = 30000; //ms

    public static final byte MAX_SPELL_LEVEL = 6;

    public static final String GUILD_SPELLS = "462;0|461;0|460;0|459;0|458;0|457;0|456;0|455;0|454;0|453;0|452;0|451;0";

    public static final Pair<Byte, Integer> ANGLE_CONDITION = new Pair<>((byte) 10, 42);

    public static final Pair<Byte, Integer> DEVIL_CONDITION = new Pair<>((byte) 10, 95);

    public static final List<Short> FIRST_BOARD = Collections.synchronizedList(Stream.of(1, 30, 59, 88, 117, 146, 175, 204, 233, 262, 291, 320, 349, 378, 407, 436, 465, 15, 44, 73, 102, 131, 160, 189, 218, 247, 276, 305, 334, 363, 392, 421, 450, 479).map(Integer::shortValue).collect(Collectors.toList()));

    public static final List<Short> SECOND_BOARD = Collections.synchronizedList(Stream.of(16, 45, 74, 103, 132, 161, 190, 219, 248, 277, 306, 335, 364, 393, 422, 451, 29, 58, 87, 116, 145, 174, 203, 232, 261, 290, 319, 348, 377, 406, 435, 464).map(Integer::shortValue).collect(Collectors.toList()));

    public static final List<Short> THIRD_BORD = Collections.synchronizedList(Stream.of(15,44,73,102,131,160,189,218,247,276,305,334,363,392,421,450).map(Integer::shortValue).collect(Collectors.toList()));

    public static final List<Short> FOURTH_BORD = Collections.synchronizedList(Stream.of(28,57,86,115,144,173,202,231,260,289,318,347,376,405,434,463).map(Integer::shortValue).collect(Collectors.toList()));

    public static final short[] LOS_DIRECTION = {1, -1, 29, -29, 15, 14, -15, -14};

    public static short WATER_OBJECT = 311;

    public static short knightLevel(short level) {
        if (level <= 50)
            return 50;
        if (Utils.range(level, 50, 80))
            return 80;
        if (Utils.range(level, 80, 110))
            return 110;
        if (Utils.range(level, 110, 140))
            return 140;
        if (Utils.range(level, 140, 200))
            return 170;
        return 170;
    }

    public static class Monster {
        public static int KNIGHT = 394;
    }

}
