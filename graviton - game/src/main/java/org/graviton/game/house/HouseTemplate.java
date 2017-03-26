package org.graviton.game.house;

import lombok.Data;
import org.graviton.xml.XMLElement;

/**
 * Created by Botan on 25/03/2017. 10:32
 */

@Data
public class HouseTemplate {
    private final short id;
    private final int gameMap, houseMap;
    private final short gameCell, houseCell;

    private final long basePrice;

    public HouseTemplate(XMLElement element) {
        this.id = element.getAttribute("id").toShort();

        this.gameMap = element.getAttribute("gameMap").toInt();
        this.houseMap = element.getAttribute("houseMap").toInt();

        this.gameCell = element.getAttribute("gameCell").toShort();
        this.houseCell = element.getAttribute("houseCell").toShort();

        this.basePrice = element.getAttribute("price").toLong();

    }
}
