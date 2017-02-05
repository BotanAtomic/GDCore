package org.graviton.game.command.commands;

import org.graviton.game.client.player.Player;
import org.graviton.game.command.api.AbstractCommand;
import org.graviton.game.command.api.Command;
import org.graviton.network.game.protocol.MessageFormatter;

/**
 * Created by Botan on 15/01/2017. 19:34
 */

@Command("send")
public class SendCommand implements AbstractCommand {

    @Override
    public String description() {
        StringBuilder builder = new StringBuilder("<u>send</u>");
        builder.append(" data").append(" > ");
        builder.append("send data to client");
        return builder.toString();
    }

    @Override
    public void apply(Player player, String data) {
        if (data.split(" ").length < 2) {
            player.send(MessageFormatter.redConsoleMessage("Invalid argument data, please follow the syntax"));
            return;
        }
        player.send(data.split(" ")[1]);
        player.send(MessageFormatter.greenConsoleMessage("Successfully send '" + data.split(" ")[1] + "' to client"));
    }
}
