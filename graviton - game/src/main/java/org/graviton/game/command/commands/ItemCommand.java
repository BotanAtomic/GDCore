package org.graviton.game.command.commands;

import org.graviton.game.client.player.Player;
import org.graviton.game.command.api.AbstractCommand;
import org.graviton.game.command.api.Command;
import org.graviton.game.items.template.ItemTemplate;


/**
 * Created by Botan on 15/01/2017. 16:41
 */

@Command("item")
public class ItemCommand implements AbstractCommand {

    @Override
    public String description() {
        StringBuilder builder = new StringBuilder("<u>item</u>");
        builder.append(" template [quantity=optional] [maximum=optional (boolean)]").append(" > ");
        builder.append("Create item by template");
        return builder.toString();
    }

    @Override
    public void apply(Player player, String[] data) {
        ItemTemplate itemTemplate = player.getEntityFactory().getItemTemplate(Short.parseShort(data[1]));
        player.getInventory().addItem(itemTemplate.createMax(player.entityFactory().getNextItemId()), true);
    }
}
