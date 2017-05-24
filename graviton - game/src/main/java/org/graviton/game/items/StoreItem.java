package org.graviton.game.items;

import lombok.Data;

/**
 * Created by Botan on 19/05/17. 22:53
 */

@Data
public class StoreItem {
    private Item item;
    private long price;

    public StoreItem(Item item, long price) {
        this.item = item;
        this.price = price;
    }

    public int getId() {
        return item.getId();
    }

}
