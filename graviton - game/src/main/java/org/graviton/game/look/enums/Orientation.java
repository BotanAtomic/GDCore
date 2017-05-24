package org.graviton.game.look.enums;

/**
 * Created by Botan on 19/11/2016 : 11:06
 */
public enum Orientation {
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST,
    NORTH,
    NORTH_EAST;

    public static final Orientation[] ADJACENT = {
            SOUTH_EAST,
            SOUTH_WEST,
            NORTH_WEST,
            NORTH_EAST
    };

    public static final Orientation[] BASIC = {
            EAST,
            SOUTH,
            NORTH,
            WEST
    };

    public static Orientation valueOf(byte ordinal) {
        return values()[ordinal];
    }

    public static Orientation random() {
        return values()[(int) (System.currentTimeMillis() % (Orientation.values().length - 1))];
    }

    public Orientation next() {
        return valueOf((byte) (ordinal() + 1));
    }

    public Orientation previous() {
        return valueOf((byte) (ordinal() - 1));
    }
}