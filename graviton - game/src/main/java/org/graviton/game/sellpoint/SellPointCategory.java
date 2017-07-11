package org.graviton.game.sellpoint;

import lombok.Data;
import org.graviton.database.entity.EntityFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Botan on 27/06/17. 20:04
 */

@Data
public class SellPointCategory {
    private final EntityFactory entityFactory;

    private final byte id;
    private final Map<Short, SellPointTemplate> templates = new ConcurrentHashMap<>();


    SellPointCategory(byte id, EntityFactory entityFactory) {
        this.id = id;
        this.entityFactory = entityFactory;
    }

    public void addTemplate(short templateId, SellPointItem sellPointItem) {
        this.templates.put(templateId, new SellPointTemplate(templateId, sellPointItem, entityFactory));
    }

    public void add(SellPointItem sellPointItem) {
        short templateId = sellPointItem.getItem().getTemplate().getId();

        if (this.templates.containsKey(templateId)) this.templates.get(templateId).add(sellPointItem);
        else this.addTemplate(templateId, sellPointItem);
    }

    public void remove(SellPointItem sellPointItem) {
        this.templates.get(sellPointItem.getItem().getTemplate().getId()).remove(sellPointItem);

        if (this.templates.get(sellPointItem.getItem().getTemplate().getId()).isEmpty()) {
            this.templates.remove(sellPointItem.getItem().getTemplate().getId());
        }
    }

    String parseTemplate() {
        StringBuilder builder = new StringBuilder();
        templates.keySet().forEach(itemId -> builder.append(itemId).append(";"));
        return builder.length() > 0 ? builder.substring(0, builder.length() - 1) : builder.toString();
    }

    public SellPointTemplate getTemplate(short template) {
        return this.templates.get(template);
    }



    public List<SellPointItem> all() {
        return templates.values().stream().map(SellPointTemplate::all).flatMap(List::stream).collect(Collectors.toList());
    }

}
