package org.graviton.network.game.protocol;

import org.graviton.game.items.Item;
import org.graviton.game.items.common.ItemPosition;

import java.util.Collection;

/**
 * Created by Botan on 05/12/2016. 17:07
 */
public class ItemPacketFormatter {

    public static String quantityMessage(int item, short quantity) {
        return "OQ" + item + '|' + quantity;
    }

    public static String formatItems(Collection<Item> items) {
        StringBuilder builder = new StringBuilder();
        items.forEach(object -> builder.append(object.parse()));
        return builder.toString();
    }

    public static String deleteMessage(int item) {
        return "OR" + item;
    }

    public static String addItemMessage(Item item) {
        return "OAKO" + item.parse();
    }

    public static String itemMovementMessage(long itemId, ItemPosition position) {
        return "OM" + itemId + "|" + (position.equippedWithoutBar() ? position.value() : "");
    }

}
