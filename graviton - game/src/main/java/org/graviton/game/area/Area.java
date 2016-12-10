package org.graviton.game.area;

import lombok.Data;
import org.graviton.xml.XMLElement;

/**
 * Created by Botan on 10/12/2016. 11:58
 */

@Data
public class Area {
    private byte id, superArea;
    private String name;

    public Area(XMLElement element) {
        this.id = element.getAttribute("id").toByte();
        this.superArea = element.getElementByTagName("super").toByte();
        this.name = element.getElementByTagName("name").toString();
    }
}
