package org.graviton.game.command.commands;

import org.graviton.game.client.player.Player;
import org.graviton.game.command.AbstractCommand;


/**
 * Created by Botan on 15/01/2017. 16:39
 */
public class HelpCommand implements AbstractCommand {
    @Override
    public String name() {
        return "help";
    }

    @Override
    public String information() {
        return name() + " > Give all commands";
    }

    @Override
    public void apply(Player player, String data) {

    }
}
