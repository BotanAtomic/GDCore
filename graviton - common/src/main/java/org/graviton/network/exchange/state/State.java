package org.graviton.network.exchange.state;

/**
 * Created by Botan on 31/10/2016 : 19:27
 */
public enum State {
    OFFLINE((byte) 0),
    ONLINE((byte) 1),
    SAVING((byte) 2);

    private final byte state;

    State(byte state) {
        this.state = state;
    }

    public static State get(byte id) {
        for (State state : State.values())
            if (state.value() == id)
                return state;
        return null;
    }

    public byte value() {
        return state;
    }

}
