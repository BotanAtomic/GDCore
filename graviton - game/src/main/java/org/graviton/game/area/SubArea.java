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
    private final List<GameMap> gameMap = new CopyOnWriteArrayList<>();
    private short id;
    private Area area;

    public SubArea(XMLElement element, Area area) {
        this.id = element.getAttribute("id").toShort();
        this.area = area;
    }

    public void registerGameMap(GameMap gameMap) {
        this.gameMap.add(gameMap);
    }
}
