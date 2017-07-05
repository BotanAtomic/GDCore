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

    public Item addItem(Item item, boolean create) {
        Item same;
        if ((same = same(item)) != null) {
            same.changeQuantity(item.getQuantity());
            player.send(ItemPacketFormatter.quantityMessage(same.getId(), same.getQuantity()));
            return same;
        } else {
            super.put(item.getId(), item);
            if (create)
                player.createItem(item);
            return null;
        }
    }

    public Item get(int item) {
        return super.get(item);
    }

    public Item same(Item item) {
        return values().stream().filter(other -> other.isSame(item) && ItemPosition.isSamePosition(other.getPosition(), item.getPosition())).findAny().orElse(null);
    }

    public Item same(Item item, ItemPosition position) {
        return values().stream().filter(other -> item.isSame(other) && position == other.getPosition()).findAny().orElse(null);
    }

    public Item getByPosition(ItemPosition position) {
        Optional<Item> same = values().stream().filter(item -> item.getPosition() == position).findFirst();
        return same.orElse(null);
    }

    public Collection<Item> getEquippedItems() {
        return values().stream().filter(item -> item.getPosition().equipped()).collect(Collectors.toList());
    }

    public void addKamas(long kamas) {
        this.kamas += kamas;
    }

    public Item haveItem(int template) {
        return values().stream().filter(item -> item.getTemplate().getId() == template).findFirst().orElse(null);
    }

    public void setItemQuantity(Item item, short quantity) {
        if(quantity <= 0) {
            player.removeItem(item);
            player.send(ItemPacketFormatter.deleteMessage(item.getId()));
        } else {
            item.setQuantity(quantity);
            player.send(ItemPacketFormatter.quantityMessage(item.getId(), quantity));
        }
    }

    public Item getItemByTemplate(short template) {
        return values().stream().filter(item -> item.getTemplate().getId() == template).findAny().orElse(null);
    }


}
