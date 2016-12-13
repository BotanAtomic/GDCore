package org.graviton.game.look;

import lombok.Data;

/**
 * Created by Botan on 11/11/2016 : 21:35
 */
@Data
abstract class AbstractLook {
    private int[] colors;
    private short skin;

    AbstractLook(int[] colors, short skin) {
        this.colors = colors;
        this.skin = skin;
    }

}
