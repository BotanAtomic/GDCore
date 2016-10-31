package org.graviton.network.exchange.state;

/**
 * Created by Botan on 31/10/2016 : 19:27
 */
public enum State {
    OFFLINE((byte) 0),
    ONLINE((byte) 2),
    SAVING((byte) 3);

    private final byte state;

    State(byte state) {
        this.state = state;
    }

    public byte value() {
        return state;
    }
}
