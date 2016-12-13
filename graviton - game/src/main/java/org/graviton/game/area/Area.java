package org.graviton.game.area;

import lombok.Data;
import org.graviton.xml.XMLElement;

/**
 * Created by Botan on 10/12/2016. 11:58
 */

@Data
public class Area {
    private final short id;
    private final byte superArea;

    public Area(XMLElement element) {
        this.id = element.getAttribute("id").toShort();
        this.superArea = element.getElementByTagName("super").toByte();
    }
}
