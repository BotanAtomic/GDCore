package org.graviton.game.action.npc;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 26/05/17. 21:22
 */

@GameAction(id= 0b110)
public class LearnJob implements Action {

    @Override
    public void apply(GameClient client, Object data) {
        System.err.println("Learn job " + String.valueOf(data));
    }

    @Override
    public void finish() {

    }
}
