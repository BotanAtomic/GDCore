package org.graviton.game.exchange.type;

import org.graviton.game.client.player.Player;
import org.graviton.game.creature.merchant.Merchant;
import org.graviton.game.exchange.Exchange;
import org.graviton.game.items.Item;
import org.graviton.game.items.StoreItem;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.network.game.protocol.ExchangePacketFormatter;
import org.graviton.network.game.protocol.ItemPacketFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

/**
 * Created by Botan on 21/05/17. 14:46
 */
public class MerchantExchange implements Exchange {
    private final Player player;
    private final Merchant merchant;

    public MerchantExchange(Merchant merchant, Player player) {
        this.merchant = merchant;
        this.player = player;

        merchant.getBusy().set(true);
    }

    private void changeItemQuantity(Item item, short newQuantity) {
        player.send(ItemPacketFormatter.quantityMessage(item.getId(), newQuantity));
        item.setQuantity(newQuantity);
    }

    @Override
    public void accept() {

    }

    @Override
    public void cancel() {
        merchant.getBusy().set(false);

        this.player.getEntityFactory().getPlayerRepository().save(player);
        this.player.getEntityFactory().getPlayerRepository().save(merchant.getPlayer());
        this.player.send(ExchangePacketFormatter.cancelMessage());

        if (merchant.getStore().isEmpty()) {
            player.getEntityFactory().getPlayerRepository().removeMerchant(merchant.getId());
            player.getMap().out(merchant);
        }
    }

    @Override
    public short getItemQuantity(int exchangerId, int itemId) {
        return 0;
    }

    @Override
    public void addItem(int itemId, short quantity, int exchangerId) {

    }

    @Override
    public void removeItem(int itemId, short quantity, int exchangerId) {

    }

    @Override
    public void editKamas(long quantity, int exchangerId) {

    }

    @Override
    public void toggle(int exchangerId) {

    }

    @Override
    public void buy(int itemId, short quantity) {
        StoreItem storedItem = merchant.getStore().getStoreItem(itemId);
        Item item = storedItem.getItem();
        Item same = player.getInventory().same(storedItem.getItem());

        short newQuantity = (short) (item.getQuantity() - quantity);

        long price = quantity * storedItem.getPrice();

        if (player.getInventory().getKamas() < price) return;

        player.getInventory().addKamas(-price);
        merchant.getPlayer().getInventory().addKamas(price);
        player.send(PlayerPacketFormatter.asMessage(player));

        if (same != null) {
            if (newQuantity <= 0) {
                merchant.getStore().remove(storedItem);
                player.getEntityFactory().getPlayerRepository().removeItem(item);
                changeItemQuantity(same, (short) (same.getQuantity() + item.getQuantity()));
                player.getEntityFactory().getPlayerRepository().saveItem(same, player.getId());

            } else {
                changeItemQuantity(same, (short) (same.getQuantity() + quantity));
                item.setQuantity(newQuantity);
                player.getEntityFactory().getPlayerRepository().saveItem(item, merchant.getId());
                player.getEntityFactory().getPlayerRepository().saveItem(same, player.getId());
            }
        } else {
            if (newQuantity <= 0) {
                merchant.getStore().remove(storedItem);
                item.setPosition(ItemPosition.NotEquipped);
                player.send(ItemPacketFormatter.addItemMessage(item));
                player.getEntityFactory().getPlayerRepository().saveItem(item, player.getId());
                player.getEntityFactory().getPlayerRepository().saveItem(item, player.getId());
            } else {
                item.setQuantity(newQuantity);
                Item clone = item.clone(player.entityFactory().getNextItemId());
                clone.setPosition(ItemPosition.NotEquipped);
                clone.setQuantity(quantity);
                player.getInventory().addItem(clone, true);
                player.send(ItemPacketFormatter.addItemMessage(clone));
                player.getEntityFactory().getPlayerRepository().saveItem(clone, player.getId());
                player.getEntityFactory().getPlayerRepository().saveItem(item, merchant.getId());
            }
        }

        player.send(ExchangePacketFormatter.personalStoreMessage(merchant.getStore()));
        player.send(ExchangePacketFormatter.buySuccessMessage());
    }

    @Override
    public void sell(int itemId, short quantity) {

    }
}
