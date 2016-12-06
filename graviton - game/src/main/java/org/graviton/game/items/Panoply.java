package org.graviton.game.items;

import lombok.Data;
import org.graviton.game.items.common.ItemEffect;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.xml.XMLElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Botan on 03/12/2016. 20:23
 */
@Data
public class Panoply {
    private final short id;
    private final String name;
    private final Map<Short, ItemTemplate> templates;
    private final Map<ItemEffect, Short> effects = new HashMap<>();

    public Panoply(XMLElement element, Map<Short, ItemTemplate> templates) {
        this.id = element.getAttribute("id").toShort();
        this.name = element.getElementByTagName("name").toString();
        this.templates = templates;

        for (String effect : element.getElementByTagName("bonus").toString().split(";")) {
            for (String finalEffect : effect.split(",")) {
                String[] effectValues = finalEffect.split(":");
                this.effects.put(ItemEffect.get(Short.parseShort(effectValues[0])), Short.parseShort(effectValues[1]));
            }
        }
    }
}
