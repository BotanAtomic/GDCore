package org.graviton.game.maps.cell;

import lombok.Data;

/**
 * Created by Botan on 12/11/2016 : 18:18
 */
@Data
public class Trigger {
    private final int nextMap;
    private final short nextCell;

    public Trigger(int nextMap, short nextCell) {
        this.nextMap = nextMap;
        this.nextCell = nextCell;
    }
}
