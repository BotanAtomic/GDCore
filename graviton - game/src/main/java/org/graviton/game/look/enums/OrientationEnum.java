package org.graviton.game.look.enums;

/**
 * Created by Botan on 19/11/2016 : 11:06
 */
public enum OrientationEnum {
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST,
    NORTH,
    NORTH_EAST;

    public static final OrientationEnum[] ADJACENTS = new OrientationEnum[]{
            SOUTH_EAST,
            SOUTH_WEST,
            NORTH_WEST,
            NORTH_EAST
    };

    public static OrientationEnum valueOf(int ordinal) {
        return values()[ordinal];
    }
}