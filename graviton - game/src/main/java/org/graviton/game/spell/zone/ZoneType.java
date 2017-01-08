package org.graviton.game.spell.zone;

/**
 * Created by Botan on 25/12/2016. 19:36
 */
public enum ZoneType {
    CROSS('X'),
    LINE('L'),
    CIRCLE('C'),
    SINGLE_CELL('P');

    char value;

    ZoneType(char value) {
        this.value = value;
    }

    public static ZoneType valueOf(char value) {
        for (ZoneType val : values()) if (val.value == value) return val;
        return SINGLE_CELL;
    }

    public char value() {
        return value;
    }
}
