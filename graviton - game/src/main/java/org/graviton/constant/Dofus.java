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

    private static final short[][] JOB_ACTIONS = {
            {101}, {6, 303}, {39, 473}, {40, 476}, {10, 460}, {141, 2357}, {139, 2358}, {37, 471}, {154, 7013}, {33, 461}, {41, 474}, {34, 449}, {174, 7925}, {155, 7016}, {38, 472}, {35, 470},
            {158, 7014}, {48}, {32}, {24, 312}, {25, 441}, {26, 442}, {28, 443}, {56, 445}, {162, 7032}, {55, 444}, {29, 350}, {31, 446}, {30, 313}, {161, 7033}, {133}, {124, 1782, 1844, 603},
            {125, 1844, 603, 1847, 1794}, {126, 603, 1847, 1794, 1779}, {127, 1847, 1794, 1779, 1801}, {128, 598, 1757, 1750}, {129, 1757, 1805, 600}, {130, 1805, 1750, 1784, 600}, {131, 600, 1805, 602, 1784},
            {136, 2187}, {140, 1759}, {140, 1799}, {23}, {68, 421}, {69, 428}, {71, 395}, {72, 380}, {73, 593}, {74, 594}, {160, 7059}, {122}, {47}, {45, 289}, {53, 400}, {57, 533}, {46, 401}, {50, 423}, {52, 532}, {159, 7018}, {58, 405}, {54, 425},
            {109}, {27}, {135}, {134}, {132}, {64}, {123}, {63}, {11}, {12}, {13}, {14}, {145}, {20}, {144}, {19}, {142}, {18}, {146}, {21}, {65}, {143}, {115}, {1}, {116}, {113}, {117}, {120}, {119}, {118}, {165}, {166}, {167},
            {163}, {164}, {169}, {168}, {171}, {182}, {15}, {149}, {17}, {147}, {16}, {148}, {156}, {151}, {110}, {121}, {22}
    };

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

    public static boolean isJobAction(short id) {
        return Stream.of(JOB_ACTIONS).filter(data -> data[0] == id).count() > 0;
    }

    public static short getObjectByAction(short action) {
        short[] result = Stream.of(JOB_ACTIONS).filter(data -> data[0] == action).findAny().orElse(null);
        return result[result.length > 2 ? (int) System.nanoTime() % result.length : result.length > 1 ? 1 : 0];
    }

    static final short[] craftExperienceData = {1, 10, 25, 50, 100, 250, 500, 1000};

    public static short calculXpWinCraft(byte level, byte maxCase) {
        return level == 100 ? 0 :  craftExperienceData[maxCase - 1];
    }

    public static class Monster {
        public static int KNIGHT = 394;
    }

}
