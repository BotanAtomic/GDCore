package org.graviton.game.action.npc;

import org.graviton.game.action.Action;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 08/12/2016. 22:31
 */
public class FinishQuest implements Action {

    @Override
    public void apply(GameClient client, Object data) {
        System.err.println("Finish quest"); //TODO : quest
        client.getBaseHandler().getDialogHandler().leaveDialog();
    }

    @Override
    public void finish() {

    }

}
