package org.graviton.game.command.commands;

import org.graviton.game.client.player.Player;
import org.graviton.game.command.AbstractCommand;


/**
 * Created by Botan on 15/01/2017. 16:41
 */
public class ItemCommand implements AbstractCommand {
    @Override
    public String name() {
        return "item";
    }

    @Override
    public String information() {
        StringBuilder builder = new StringBuilder(name());
        builder.append(" (item);(quantity = optional);(maximum [true/false] = optional)").append(" > ");
        builder.append("Create item by template identifier");
        return builder.toString();
    }

    @Override
    public void apply(Player player, String data) {

    }
}
