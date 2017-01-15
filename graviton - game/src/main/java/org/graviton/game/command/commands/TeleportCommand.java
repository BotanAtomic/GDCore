package org.graviton.game.command.commands;

import org.graviton.game.client.player.Player;
import org.graviton.game.command.AbstractCommand;

/**
 * Created by Botan on 15/01/2017. 16:10
 */
public class TeleportCommand implements AbstractCommand {

    @Override
    public String name() {
        return "teleport";
    }

    @Override
    public String information() {
        StringBuilder builder = new StringBuilder(name());
        builder.append(" (player = optional);(map);(cell = optional)").append(" > ");
        builder.append("Teleport player to selected map / cell");
        return builder.toString();
    }

    @Override
    public void apply(Player player, String data) {
        System.err.println("Teleportation !!!");
    }
}
