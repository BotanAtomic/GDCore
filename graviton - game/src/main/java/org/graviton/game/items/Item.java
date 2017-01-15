package org.graviton.game.items;

import lombok.Data;
import org.graviton.game.items.common.Bonus;
import org.graviton.game.items.common.ItemEffect;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.game.items.template.ItemTemplate;
import org.jooq.Record;

import java.util.TreeMap;

import static org.graviton.database.jooq.game.tables.Items.ITEMS;

/**
 * Created by Botan on 03/12/2016. 20:24
 */

@Data
public class Item {
    private int id;

    private ItemTemplate template;
    private ItemPosition position;

    private short quantity;
    private TreeMap<ItemEffect, Short> statistics;

    public Item(Record record, ItemTemplate template) {
        this.id = record.get(ITEMS.ID);
        this.template = template;
        this.position = ItemPosition.get(record.get(ITEMS.POSITION));
        this.quantity = record.get(ITEMS.QUANTITY);
        this.statistics = this.template.getEffectByString(record.get(ITEMS.STATISTICS));
    }

    public Item(int id, ItemTemplate template, ItemPosition position, TreeMap<ItemEffect, Short> statistics) {
        this.id = id;
        this.template = template;
        this.position = position;
        this.statistics = statistics;
        this.quantity = 1;
    }

    public Item clone(int nextId) {
        return new Item(nextId, template, position, statistics);
    }

    public String parse() {
        StringBuilder builder = new StringBuilder();
        String position = !this.position.equippedWithoutBar() ? "-1" : Integer.toHexString(this.position.value());
        builder.append(Integer.toHexString(id)).append('~').append(Integer.toHexString(template.getId())).append('~').
                append(Integer.toHexString(quantity)).append('~').append(position).append('~').
                append(this.parseEffects()).append(';');
        return builder.toString();
    }

    public String parseEffects() {
        final StringBuilder builder = new StringBuilder();
        this.statistics.descendingKeySet().forEach(i -> {
            if (i.isWeaponEffect()) {
                Bonus bonus = template.getEffects().get(i);
                builder.append(Integer.toHexString(i.value())).append("#");
                builder.append(Integer.toHexString(bonus.min())).append('#');
                builder.append(Integer.toHexString(bonus.max())).append('#');
                builder.append("0#");
                builder.append(bonus.toString()).append(',');
            } else {
                final short value = this.statistics.get(i);
                builder.append(Integer.toHexString(i.value())).append("#").append(Integer.toHexString((int) value)).append("#0#0#").append("0d0+").append(value).append(',');
            }
        });
        return builder.toString().substring(0, builder.length() == 0 ? 0 : builder.length() - 1);
    }

    public void changeQuantity(short quantity) {
        this.quantity += quantity;
    }
}
