package org.graviton.game.area;

import lombok.Data;
import org.graviton.game.maps.GameMap;
import org.graviton.xml.XMLElement;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Botan on 10/12/2016. 11:58
 */

@Data
public class SubArea {
    private final List<GameMap> gameMaps;
    private short id;
    private Area area;

    public SubArea(XMLElement element, Area area) {
        this.gameMaps = new CopyOnWriteArrayList<>();
        this.id = element.getAttribute("id").toShort();
        (this.area = area).getSubAreas().add(this);
    }

    public void registerGameMap(GameMap gameMap) {
        this.gameMaps.add(gameMap);
    }
}
