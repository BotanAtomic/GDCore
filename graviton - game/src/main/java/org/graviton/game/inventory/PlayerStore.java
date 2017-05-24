package org.graviton.game.inventory;

import org.graviton.game.client.player.Player;
import org.graviton.game.items.Item;
import org.graviton.game.items.StoreItem;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.network.game.protocol.ExchangePacketFormatter;
import org.graviton.network.game.protocol.ItemPacketFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Botan on 18/05/17. 21:55
 */
public class PlayerStore extends ArrayList<StoreItem> {
    private final Player player;

    public PlayerStore(Player player, List<StoreItem> items) {
        super.addAll(items);
        this.player = player;
    }

    private void changeItemQuantity(Item item, short newQuantity) {
        player.send(ItemPacketFormatter.quantityMessage(item.getId(), newQuantity));
        item.setQuantity(newQuantity);
    }

    private void removeItem(Item item, boolean partial) {
        player.send(ItemPacketFormatter.deleteMessage(item.getId()));
        if (partial)
            player.getInventory().remove(item.getId());
        else
            player.removeItem(item);
    }

    private void saveItems(Item... items) {
        Stream.of(items).forEach(item -> player.getEntityFactory().getPlayerRepository().saveItem(item, player.getId()));
    }

    private Item same(Item item, long price) {
        return stream().filter(storedItem -> storedItem.getItem().isSame(item) && storedItem.getPrice() == price).map(StoreItem::getItem).findAny().orElse(null);
    }

    public void changePrice(int itemId, long newPrice) {
        StoreItem storedItem = getStoreItem(itemId);

        if (storedItem != null) {
            Item same = same(storedItem.getItem(), newPrice);
            if (same != null) {
                same.setQuantity((short) (same.getQuantity() + storedItem.getItem().getQuantity()));
                remove(storedItem);
                player.getEntityFactory().getPlayerRepository().removeItem(storedItem.getItem());
                saveItems(same);
            } else
                storedItem.setPrice(newPrice);

            player.send(ExchangePacketFormatter.personalStoreMessage(this));
        }
    }

    public StoreItem getStoreItem(int itemId) {
        return stream().filter(stored -> itemId == stored.getId()).findAny().orElse(null);
    }

    public void removeStoreItem(int itemId) {
        StoreItem storedItem = getStoreItem(itemId);
        Item same = player.getInventory().same(storedItem.getItem());

        if(same != null) {
            changeItemQuantity(same, (short) (same.getQuantity() + storedItem.getItem().getQuantity()));
            remove(storedItem);
            player.getEntityFactory().getPlayerRepository().removeItem(storedItem.getItem());
            saveItems(same);
        } else {
            player.send(ItemPacketFormatter.addItemMessage(storedItem.getItem()));
            storedItem.getItem().setPosition(ItemPosition.NotEquipped);
            player.getInventory().addItem(storedItem.getItem(), false);
            remove(storedItem);
            saveItems(storedItem.getItem());
        }
        player.send(ExchangePacketFormatter.personalStoreMessage(this));
    }

    public void add(Item item, short quantity, long price) {
        Item same = same(item, price);

        short newQuantity = (short) (item.getQuantity() - quantity);

        if (same != null) {
            if (newQuantity <= 0) {
                removeItem(item, false);
                same.setQuantity((short) (same.getQuantity() + item.getQuantity()));
                saveItems(same);
            } else {
                changeItemQuantity(item, newQuantity);
                same.setQuantity((short) (same.getQuantity() + quantity));
            }
        } else {
            if (newQuantity <= 0) {
                item.setPosition(ItemPosition.Store);
                removeItem(item, true);
                super.add(new StoreItem(item, price));
                saveItems(item);
            } else {
                changeItemQuantity(item, newQuantity);
                Item clone = item.clone(player.getEntityFactory().getNextItemId());
                clone.setQuantity(quantity);
                super.add(new StoreItem(clone, price));
                clone.setPosition(ItemPosition.Store);
                saveItems(item, clone);
                player.getEntityFactory().getPlayerRepository().createItem(clone, player.getId());
            }
        }
        player.send(ExchangePacketFormatter.personalStoreMessage(this));
    }

    public long getTax() {
        return (long) (stream().mapToDouble(StoreItem::getPrice).sum() / 1000);
    }

    public Player getPlayer() {
        return this.player;
    }

}
