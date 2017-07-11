package org.graviton.game.exchange.type;

import org.graviton.game.client.player.Player;
import org.graviton.game.exchange.Exchange;
import org.graviton.network.game.protocol.ExchangePacketFormatter;

/**
 * Created by Botan on 11/07/17. 03:03
 */
public class CancelableExchange implements Exchange {
    private final Player player;

    public CancelableExchange(Player player) {
        this.player = player;
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
