package org.graviton.game.filter.type;

import org.graviton.game.client.player.Player;
import org.graviton.game.filter.Filter;
import org.graviton.game.filter.enums.FilterType;
import org.graviton.game.items.Item;

/**
 * Created by Botan on 14/07/17. 02:43
 */
public class ItemFilter implements Filter {

    @Override
    public boolean check(Player player, FilterType filterType, String data) {
        short itemTemplate;
        short quantity = 1;

        if(data.contains(",")) {
            itemTemplate = Short.parseShort(data.split(",")[0]);
            quantity = Short.parseShort(data.split(",")[1]);
        } else itemTemplate = Short.parseShort(data);

        switch (filterType) {
            case DIFFERENT:
                return player.getInventory().getItemByTemplate(itemTemplate) == null;
            case EQUALS:
                Item item = player.getInventory().getItemByTemplate(itemTemplate);
                return item != null && item.getQuantity() >= quantity;
        }
        return false;
    }
}
