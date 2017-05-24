package org.graviton.game.trunk;

import org.graviton.game.items.Item;

import java.util.ArrayList;

/**
 * Created by Botan on 13/05/17. 19:20
 */
public abstract class AbstractTrunk extends ArrayList<Item>{
    private long kamas;

    protected AbstractTrunk(long kamas) {
        this.kamas = kamas;
    }

    public String compileItems() {
        StringBuilder builder = new StringBuilder();
        super.forEach(item -> builder.append(item.getId()).append(';'));
        return builder.length() == 0 ? builder.toString() : builder.substring(0, builder.length() - 1);
    }

    public Item same(Item other) {
        return stream().filter(item -> item.isSame(other)).findFirst().orElse(null);
    }

    public void addItem(Item item) {
        super.add(item);
    }

    public Item getItem(int itemId) {
        return stream().filter(item -> item.getId() == itemId).findAny().orElse(null);
    }

    public void removeItem(Item item) {
        remove(item);
    }

    public void setKamas(long kamas) {
        this.kamas = kamas;
    }

    public long getKamas() {
        return this.kamas;
    }

}
