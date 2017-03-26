package org.graviton.game.inventory;

import org.graviton.game.creature.collector.Collector;
import org.graviton.game.items.Item;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Botan on 10/03/2017. 22:58
 */
public class CollectorInventory extends ConcurrentHashMap<Integer, Item> {
    private final Collector collector;

    public CollectorInventory(Collector collector) {
        this.collector = collector;
    }

}
