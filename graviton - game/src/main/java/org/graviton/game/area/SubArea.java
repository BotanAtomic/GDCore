package org.graviton.game.area;

import lombok.Data;
import org.graviton.xml.XMLElement;

/**
 * Created by Botan on 10/12/2016. 11:58
 */

@Data
public class SubArea {
    private short id;
    private String name;
    private Area area;

    public SubArea(XMLElement element, Area area) {
        this.id = element.getAttribute("id").toShort();
        this.name = element.getElementByTagName("name").toString();
        this.area = area;
    }
}
