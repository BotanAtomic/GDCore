package org.graviton.game.maps.object;

/**
 * Created by Botan on 23/03/2017. 19:41
 */
public enum InteractiveObjectState {
    DEFAULT,
    FULL,
    EMPTYING,
    EMPTY,
    EMPTY2,
    FULLING;

    public byte id() {
        return (byte) ordinal();
    }

}
