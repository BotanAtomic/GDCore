package org.graviton.game.hdv;

import lombok.Data;
import org.graviton.utils.Utils;
import org.graviton.xml.XMLElement;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Botan on 26/06/17. 22:56
 */

@Data
public class SellPoint {
    private final byte id, tax;
    private final short expiration, level;
    private final String categoriesData;
    private final Map<Byte, SellPointContainer> categories;

    public SellPoint(XMLElement element) {
        this.id = element.getAttribute("id").toByte();
        this.tax = element.getAttribute("tax").toByte();
        this.expiration = element.getAttribute("expiration").toShort();
        this.level = element.getAttribute("level").toShort();
        this.categoriesData = element.getAttribute("categories").toString();
        this.categories = Utils.arrayToByteList(categoriesData, ",").stream().collect(Collectors.toMap(b -> b, SellPointContainer::new));
    }


}
