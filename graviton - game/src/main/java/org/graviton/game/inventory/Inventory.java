package org.graviton.game.inventory;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.game.items.Item;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.network.game.protocol.ItemPacketFormatter;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Botan on 04/12/2016. 12:27
 */

@Data
public class Inventory extends ConcurrentHashMap<Integer, Item> {
    private final Player player;
    private long kamas;

    public Inventory(Player player, long kamas, Map<Integer, Item> items) {
        super.putAll(items);
        this.kamas = kamas;
        this.player = player;
    }

    public Inventory(Player player) {
        this.kamas = 0;
        this.player = player;
    }

    public void addItem(Item item, boolean create) {
        Item same;
        if ((same = same(item)) != null) {
            same.changeQuantity(item.getQuantity());
            player.send(ItemPacketFormatter.quantityMessage(same.getId(), same.getQuantity()));
        } else {
            super.put(item.getId(), item);
            if (create)
                player.createItem(item);
        }
    }

    public Item get(int item) {
        return super.get(item);
    }

    private Item same(Item item) {
        Optional<Item> same = values().stream().filter(item1 -> item1.getTemplate().getId() == item.getTemplate().getId()).
                filter(item1 -> item1.getStatistics().equals(item.getStatistics()) &&
                        item.getPosition() == item1.getPosition()).findFirst();
        return same.isPresent() ? same.get() : null;
    }

    public Item same(Item item, ItemPosition position) {
        Optional<Item> same = values().stream().filter(item1 -> item1.getTemplate().getId() == item.getTemplate().getId()).
                filter(item1 -> item1.getStatistics().equals(item.getStatistics()) &&
                        item1.getPosition() == position).findFirst();
        return same.isPresent() ? same.get() : null;
    }

    public Item getByPosition(ItemPosition position) {
        Optional<Item> same = values().stream().filter(item -> item.getPosition() == position).findFirst();
        return same.isPresent() ? same.get() : null;
    }

    public Collection<Item> getEquippedItems() {
        return values().stream().filter(item -> item.getPosition().equipped()).collect(Collectors.toList());
    }

}
