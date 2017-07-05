package org.graviton.game.area;

import lombok.Data;
import org.graviton.game.maps.GameMap;
import org.graviton.xml.XMLElement;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by Botan on 10/12/2016. 11:58
 */

@Data
public class Area {
    private final short id;
    private final byte superArea;

    private List<SubArea> subAreas = new CopyOnWriteArrayList<>();

    public Area(XMLElement element) {
        this.id = element.getAttribute("id").toShort();
        this.superArea = element.getElementByTagName("super").toByte();
    }

    public List<GameMap> getGameMaps() {
        return subAreas.stream().map(SubArea::getGameMaps).flatMap(List::stream).collect(Collectors.toList());
    }
}
