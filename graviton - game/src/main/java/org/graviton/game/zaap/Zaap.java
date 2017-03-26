package org.graviton.game.zaap;

import lombok.Data;
import org.graviton.xml.XMLElement;

/**
 * Created by Botan on 26/03/2017. 17:26
 */

@Data
public class Zaap {
    private int gameMap;
    private short cell;

    public Zaap(XMLElement element) {
        this.gameMap = element.getAttribute("map").toInt();
        this.cell = element.getAttribute("cell").toShort();
    }

}
