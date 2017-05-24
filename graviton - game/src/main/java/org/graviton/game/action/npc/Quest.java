package org.graviton.game.action.npc;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 08/12/2016. 21:42
 */

@GameAction(id=40)
public class Quest implements Action {
    @Override
    public void apply(GameClient client, Object data) {
        System.err.println("New quest"); //TODO : quest
        client.getBaseHandler().getDialogHandler().leaveDialog();
    }

    @Override
    public void finish() {

    }
}
