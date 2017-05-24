package org.graviton.game.action.npc;

import org.graviton.game.action.Action;
import org.graviton.game.client.player.Player;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 12/03/2017. 14:17
 */
public class AlignmentCondition implements Action { //TODO

    @Override
    public void apply(GameClient client, Object data) {
        String argument = (String) data;
        Player player = client.getPlayer();
        byte alignment = Byte.parseByte(argument.split(",")[0]);
        int requiredMap = Integer.parseInt(argument.split(",")[1]);
    }

    @Override
    public void finish() {

    }

}
