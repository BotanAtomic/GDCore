package org.graviton.game.position;

import lombok.Data;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.look.enums.Orientation;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.cell.Cell;

/**
 * Created by Botan on 13/11/2016 : 18:46
 */
@Data
public class Location {
    private AbstractMap map;
    private Cell cell;
    private Orientation orientation;

    public Location(AbstractMap gameMap, short cell, byte orientation) {
        this.map = gameMap;
        this.cell = gameMap.getCells().get(cell);
        this.orientation = Orientation.valueOf(orientation);
    }

    public Location(AbstractMap gameMap, short cell, Orientation orientation) {
        this.map = gameMap;
        this.cell = gameMap.getCells().get(cell);
        this.orientation = orientation;
    }

    public Location(String savedLocation, EntityFactory entityFactory) {
        String[] location = savedLocation.split(";");
        this.map = entityFactory.getMap(Integer.parseInt(location[0]));
        this.cell = map.getCells().get(Short.parseShort(location[1]));
        this.orientation = Orientation.random();
    }

    private Location() {

    }

    public static Location empty() {
        return new Location();
    }

    public Location copy() {
        return new Location(map, cell.getId(), (byte) orientation.ordinal());
    }
}
