package org.graviton.game.command.commands;

import org.graviton.game.client.player.Player;
import org.graviton.game.command.api.AbstractCommand;
import org.graviton.game.command.api.Command;
import org.graviton.network.game.protocol.MessageFormatter;

/**
 * Created by Botan on 09/02/2017. 19:38
 */

@Command("level")
public class LevelCommand implements AbstractCommand {

    @Override
    public String description() {
        return "";
    }

    @Override
    public void apply(Player player, String[] data) {
        short level = (short) Integer.parseInt(data[1]);

        if(level <= player.getLevel()) {
            player.send(MessageFormatter.redConsoleMessage("You must choose a level higher than your"));
            return;
        }
        if(level > 200) {
            player.send(MessageFormatter.redConsoleMessage("You must choose a level less than or equal to 200"));
            return;
        }

        while (player.getLevel() < level)
            player.upgrade();

        player.upLevel(false);

        player.send(MessageFormatter.greenConsoleMessage("Successfully upgraded to level " + level));
    }
}
