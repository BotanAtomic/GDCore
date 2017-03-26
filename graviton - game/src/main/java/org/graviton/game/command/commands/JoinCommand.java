package org.graviton.game.command.commands;

import org.graviton.game.client.player.Player;
import org.graviton.game.command.api.AbstractCommand;
import org.graviton.game.command.api.Command;
import org.graviton.network.game.protocol.MessageFormatter;

/**
 * Created by Botan on 15/01/2017. 16:10
 */

@Command("join")
public class JoinCommand implements AbstractCommand {

    @Override
    public String description() {
        StringBuilder builder = new StringBuilder("<u>join</u> ");
        builder.append("[player]").append(" > ");
        builder.append("Join selected player");
        return builder.toString();
    }

    @Override
    public void apply(Player player, String[] data) {
        Player target = player.getEntityFactory().getPlayerRepository().find(data[1]);
        if(target == null)
            player.send(MessageFormatter.redConsoleMessage("Cannot find player named <b>" + data[1] + "</b>"));
         else
            player.changeMap(target.getGameMap(), target.getCell().getId());
    }
}
