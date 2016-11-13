package org.graviton.game.position;

import lombok.Data;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;

/**
 * Created by Botan on 13/11/2016 : 18:46
 */
@Data
public class Location {
    private GameMap gameMap;
    private Cell cell;

    public Location(GameMap gameMap, short cell) {
        this.gameMap = gameMap;
        this.cell = gameMap.getCells().get(cell);
    }
}
