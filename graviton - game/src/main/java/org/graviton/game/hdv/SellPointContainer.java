package org.graviton.game.hdv;

import lombok.Data;

/**
 * Created by Botan on 27/06/17. 20:04
 */

@Data
public class SellPointContainer {
    private final byte id;


    public SellPointContainer(byte id) {
        this.id = id;
    }

}
