package org.graviton.game.creature.npc.exchange;

/**
 * Created by Botan on 24/05/17. 15:34
 */
public class ItemExchangeTemplate {
    private final short itemId;
    private final short quantity;

    ItemExchangeTemplate(short itemId, short quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public short getItemId() {
        return itemId;
    }

    public short getQuantity() {
        return quantity;
    }
}
