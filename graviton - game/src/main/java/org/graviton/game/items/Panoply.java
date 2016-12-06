package org.graviton.game.items;

import lombok.Data;
import org.graviton.game.items.common.ItemEffect;
import org.graviton.game.items.template.ItemTemplate;
import org.jooq.Record;

import java.util.HashMap;
import java.util.Map;

import static org.graviton.database.jooq.game.tables.Panoply.PANOPLY;

/**
 * Created by Botan on 03/12/2016. 20:23
 */
@Data
public class Panoply {
    private final short id;
    private final String name;
    private final Map<Short, ItemTemplate> templates;
    private final Map<ItemEffect, Short> effects = new HashMap<>();

    public Panoply(Record record, Map<Short, ItemTemplate> templates) {
        this.id = record.get(PANOPLY.ID);
        this.name = record.get(PANOPLY.NAME);
        this.templates = templates;

        for (String effect : record.get(PANOPLY.BONUS).split(";")) {
            for (String finalEffect : effect.split(",")) {
                String[] effectValues = finalEffect.split(":");
                this.effects.put(ItemEffect.get(Short.parseShort(effectValues[0])), Short.parseShort(effectValues[1]));
            }
        }
    }
}
