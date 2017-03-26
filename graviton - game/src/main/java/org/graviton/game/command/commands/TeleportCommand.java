package org.graviton.game.command.commands;

import org.graviton.game.client.player.Player;
import org.graviton.game.command.api.AbstractCommand;
import org.graviton.game.command.api.Command;

/**
 * Created by Botan on 15/01/2017. 16:10
 */

@Command("teleport")
public class TeleportCommand implements AbstractCommand {

    @Override
    public String description() {
        StringBuilder builder = new StringBuilder("<u>teleport</u> ");
        builder.append("map [cell=optional] [player=optional] ").append(" > ");
        builder.append("Teleport player to map / cell");
        return builder.toString();
    }

    @Override
    public void apply(Player player, String[] data) {
        player.changeMap(Integer.parseInt(data[1]), (short) 0);
    }
}
