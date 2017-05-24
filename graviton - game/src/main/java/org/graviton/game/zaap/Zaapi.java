package org.graviton.game.zaap;

import lombok.Data;
import org.graviton.game.alignment.type.AlignmentType;
import org.graviton.xml.XMLElement;

/**
 * Created by Botan on 13/05/17. 13:40
 */

@Data
public class Zaapi {
    private final int gameMap;
    private final short cell;
    private final AlignmentType alignment;

    public Zaapi(XMLElement element) {
        this.gameMap = element.getAttribute("map").toInt();
        this.alignment = AlignmentType.get(element.getAttribute("align").toByte());
        this.cell = element.getAttribute("cell").toShort();
    }

    @Override
    public boolean equals(Object other) {
        return ((Zaapi) other).gameMap == this.gameMap;
    }
}
