package org.graviton.game.interaction.actions;

import org.graviton.game.action.map.MapAction;
import org.graviton.game.interaction.AbstractGameAction;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 26/03/2017. 00:47
 */
public class HouseAction implements AbstractGameAction {

    public HouseAction(GameClient client, short action) {
        MapAction.get(action).apply(client ,null);
    }

    @Override
    public boolean begin() {
        return true;
    }

    @Override
    public void cancel(String data) {

    }

    @Override
    public void finish(String data) {

    }
}
