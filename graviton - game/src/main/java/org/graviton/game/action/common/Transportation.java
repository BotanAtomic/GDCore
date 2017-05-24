package org.graviton.game.action.common;

import org.graviton.game.action.Action;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 08/12/2016. 17:48
 */

@GameAction(id=0)
public class Transportation implements Action {

    @Override
    public void apply(GameClient client, Object data) {
        String[] parameter = ((String) data).split(",");

        if (parameter.length > 2 && !checkAccess(client, Integer.parseInt(parameter[2])))
            return;

        client.getPlayer().changeMap(Integer.parseInt(parameter[0]), Short.parseShort(parameter[1]));
    }

    @Override
    public void finish() {

    }

    private boolean checkAccess(GameClient client, int restrictedGameMap) {
        return client.getPlayer().getMap().getId() == restrictedGameMap;
    }
}
