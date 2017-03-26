package org.graviton.game.command.commands;

import org.graviton.game.client.player.Player;
import org.graviton.game.command.api.AbstractCommand;
import org.graviton.game.command.api.Command;

/**
 * Created by Botan on 12/03/2017. 14:36
 */

@Command("alignment")
public class AlignmentCommand implements AbstractCommand{
    @Override
    public String description() {
        return "";
    }

    @Override
    public void apply(Player player, String[] data) {
        player.changeAlignment(Byte.parseByte(data[1]));
    }
}
