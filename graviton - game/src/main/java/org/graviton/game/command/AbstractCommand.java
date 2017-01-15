package org.graviton.game.command;

import org.graviton.game.client.player.Player;

/**
 * Created by Botan on 15/01/2017. 16:01
 */
public interface AbstractCommand {

    String name();

    String information();

    void apply(Player player, String data);
}
