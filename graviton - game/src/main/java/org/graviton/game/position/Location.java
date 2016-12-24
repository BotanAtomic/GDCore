package org.graviton.game.position;

import lombok.Data;
import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;

/**
 * Created by Botan on 13/11/2016 : 18:46
 */
@Data
public class Location {
    private AbstractMap map;
    private Cell cell;
    private OrientationEnum orientation;

    public Location(GameMap gameMap, short cell, byte orientation) {
        this.map = gameMap;
        this.cell = gameMap.getCells().get(cell);
        this.orientation = OrientationEnum.valueOf(orientation);
    }

    public Location(GameMap gameMap, short cell, OrientationEnum orientation) {
        this.map = gameMap;
        this.cell = gameMap.getCells().get(cell);
        this.orientation = orientation;
    }

    public Location copy() {
        return new Location((GameMap) map, cell.getId(), (byte) orientation.ordinal());
    }
}
