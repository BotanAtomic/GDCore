package org.graviton.game.command.commands;

import org.graviton.game.client.player.Player;
import org.graviton.game.command.api.AbstractCommand;
import org.graviton.game.command.api.Command;
import org.graviton.network.game.protocol.MessageFormatter;


/**
 * Created by Botan on 15/01/2017. 16:39
 */

@Command("help")
public class HelpCommand implements AbstractCommand {

    @Override
    public String description() {
        return "<u>help</u> > Show all commands";
    }

    @Override
    public void apply(Player player, String[] data) {
        StringBuilder builder = new StringBuilder("List of all commands : \n");
        player.getEntityFactory().getCommandRepository().stream().forEach(command -> builder.append(command.description()).append("\n"));
        player.send(MessageFormatter.whiteConsoleMessage(builder.toString()));
    }
}
