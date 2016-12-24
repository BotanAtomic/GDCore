package org.graviton.game.fight.flag;

/**
 * Created by Botan on 19/12/2016. 21:43
 */
public enum FlagAttribute {
    NEED_HELP('H'),
    DENY_ALL('N'),
    ALLOW_PARTY('P'),
    DENY_SPECTATORS('S');

    private char value;

    FlagAttribute(char value) {
        this.value = value;
    }

    public char value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
