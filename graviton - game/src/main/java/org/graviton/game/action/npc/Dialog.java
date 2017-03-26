package org.graviton.game.action.npc;

import org.graviton.game.action.Action;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 08/12/2016. 17:42
 */
public class Dialog implements Action {

    @Override
    public void apply(GameClient client, Object data) {
        String argument = (String) data;
        client.getBaseHandler().getDialogHandler().createQuestion(argument);
    }

    @Override
    public void finish() {

    }

}
