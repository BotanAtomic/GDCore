package org.graviton.game.items;

import lombok.Data;
import org.graviton.game.items.common.ItemEffect;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.xml.XMLElement;

import java.util.Collection;
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
    private final Map<Byte, Map<ItemEffect, Short>> effects = new HashMap<>();

    public Panoply(XMLElement element, Map<Short, ItemTemplate> templates) {
        this.id = element.getAttribute("id").toShort();
        this.name = element.getElementByTagName("name").toString();
        this.templates = templates;

        this.templates.values().forEach(template -> template.setPanoply(this));

        byte value = 1;
        for (String effect : element.getElementByTagName("bonus").toString().split(";")) {
            Map<ItemEffect, Short> effects = new HashMap<>();
            value++;

            for (String finalEffect : effect.split(","))
                effects.put(ItemEffect.get(Short.parseShort(finalEffect.split(":")[0])), Short.parseShort(finalEffect.split(":")[1]));

            this.effects.put(value, effects);
        }
    }

    public byte getEquippedObject(Collection<Item> items) {
        return (byte) items.stream().filter(item -> item.getTemplate().getPanoply().getId() == this.id).count();
    }

    public Map<ItemEffect, Short> effects(byte equipped) {
        return this.effects.get(equipped);
    }

    public String effectToString(Map<ItemEffect, Short> effects) {
        StringBuilder builder = new StringBuilder();

        if (effects == null)
            return "";

        effects.forEach((effect, value) -> builder.append(Integer.toHexString(effect.value())).append('#').append(Integer.toHexString(value)).append("#0#0"));

        return builder.substring(0, builder.length() - 1);
    }

}
