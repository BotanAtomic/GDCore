package org.graviton.game.spell.common;

/**
 * Created by Botan on 02/01/2017. 20:06
 */
public enum Target {
    ALL((byte) -1),
    ALLY((byte) 0),
    ENEMY((byte) 1),
    PLAYER((byte) 2),
    PLAYER_AND_ALLY((byte) 3),
    INVOCATION((byte) 4),
    EXCLUDE_PLAYER((byte) 5),
    ALLY_EXCLUDE_PLAYER((byte) 5);

    private byte value;

    Target(byte value) {
        this.value = value;
    }

    public static Target get(int value) {
        return values()[value + 1];
    }

}
