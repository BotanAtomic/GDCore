package org.graviton.game.exchange.type;

import org.graviton.game.client.player.Player;
import org.graviton.game.exchange.Exchange;
import org.graviton.game.items.Item;
import org.graviton.game.items.StoreItem;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.network.game.protocol.ExchangePacketFormatter;

/**
 * Created by Botan on 18/05/17. 21:49
 */
public class MyStoreExchange implements Exchange {
    private final Player player;

    public MyStoreExchange(Player player) {
        this.player = player;
    }

    @Override
    public void accept() {

    }

    @Override
    public void cancel() {
        player.send(ExchangePacketFormatter.cancelMessage());
        player.getEntityFactory().getPlayerRepository().save(player);
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
       player.getStore().removeStoreItem(itemId);
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
