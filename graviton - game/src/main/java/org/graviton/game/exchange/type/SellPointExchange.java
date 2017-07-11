package org.graviton.game.exchange.type;

import org.graviton.game.client.player.Player;
import org.graviton.game.exchange.Exchange;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.game.sellpoint.SellPoint;
import org.graviton.game.sellpoint.SellPointItem;
import org.graviton.network.game.protocol.ExchangePacketFormatter;
import org.graviton.network.game.protocol.ItemPacketFormatter;
import org.graviton.network.game.protocol.SellPointPacketFormatter;

/**
 * Created by Botan on 11/07/17. 05:16
 */
public class SellPointExchange implements Exchange {
    private final Player player;
    private final SellPoint sellPoint;

    public SellPointExchange(Player player, SellPoint sellPoint) {
        this.player = player;
        this.sellPoint = sellPoint;
    }

    @Override
    public void accept() {

    }

    @Override
    public void cancel() {
        this.player.send(ExchangePacketFormatter.cancelMessage());
        this.player.setExchange(null);
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
        SellPointItem sellPointItem = sellPoint.itemsByPlayer(player).stream().filter(item -> item.getLineId() == itemId && item.getItem().getQuantity() == quantity).findAny().orElse(null);

        if (sellPointItem != null) {
            if (player.getInventory().addItem(sellPointItem.getItem(), false) != null)
                player.removeItem(sellPointItem.getItem());
            else
                player.send(ItemPacketFormatter.addItemMessage(sellPointItem.getItem()));

            sellPointItem.getItem().setPosition(ItemPosition.NotEquipped);
            sellPoint.remove(sellPointItem);
            player.update();
        }
        player.send(SellPointPacketFormatter.sellItemsMessage(sellPoint.itemsByPlayer(player)));
    }

    @Override
    public void editKamas(long quantity, int exchangerId) {

    }

    @Override
    public void toggle(int exchangerId) {

    }

    @Override
    public void buy(int itemId, short quantity) {

    }

    @Override
    public void sell(int itemId, short quantity) {

    }
}
