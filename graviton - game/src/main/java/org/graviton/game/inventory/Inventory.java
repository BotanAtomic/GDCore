package org.graviton.game.inventory;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.game.items.Item;
import org.graviton.network.game.protocol.ItemProtocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Botan on 04/12/2016. 12:27
 */

@Data
public class Inventory {
    private final Map<Integer, Item> items;
    private final Player player;
    private long kamas;

    public Inventory(Player player, long kamas, Map<Integer, Item> items) {
        this.items = items;
        this.kamas = kamas;
        this.player = player;
    }

    public Inventory(Player player) {
        this.items = new HashMap<>();
        this.kamas = 0;
        this.player = player;
    }

    public void addItem(Item item, boolean create) {
        Item same;
        if ((same = same(item)) != null) {
            same.changeQuantity(item.getQuantity());
            player.send(ItemProtocol.quantityMessage(same.getId(), same.getQuantity()));
        } else {
            this.items.put(item.getId(), item);
            if (create)
                player.createItem(item);
        }
    }

    public Item get(int item) {
        return this.items.get(item);
    }

    public Item same(Item item) {
        Optional<Item> same = this.items.values().stream().filter(item1 -> item1.getTemplate().getId() == item.getTemplate().getId()).
                filter(item1 -> item1.getStatistics().equals(item.getStatistics())).findFirst();
        return same.isPresent() ? same.get() : null;
    }

}
