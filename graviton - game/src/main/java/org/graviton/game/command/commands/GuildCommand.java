package org.graviton.game.command.commands;

import org.graviton.game.client.player.Player;
import org.graviton.game.command.api.AbstractCommand;
import org.graviton.game.command.api.Command;

/**
 * Created by Botan on 09/03/2017. 20:05
 */

@Command("guild")
public class GuildCommand implements AbstractCommand {

    @Override
    public String description() {
        return null;
    }

    @Override
    public void apply(Player player, String[] data) {

        switch (data[1].toLowerCase()) {
            case "experience" :
                player.getGuild().addExperience(Long.parseLong(data[2]), player.entityFactory());
                break;
        }
    }
}
