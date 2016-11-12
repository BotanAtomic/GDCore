package org.graviton.game.statistics;


import lombok.Getter;
import lombok.Setter;

/**
 * Created by Botan on 11/11/2016 : 21:12
 */
public class PlayerLife {
    @Getter
    @Setter
    private short current, max;

    public PlayerLife(byte percent, short max) {
        this.max = max;
        this.current = (short) (max * (percent / 100));
    }
}
