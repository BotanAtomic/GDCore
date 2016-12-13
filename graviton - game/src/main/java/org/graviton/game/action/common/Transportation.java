package org.graviton.game.action.common;

import org.graviton.game.action.Action;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 08/12/2016. 17:48
 */
public class Transportation implements Action {

    @Override
    public void apply(GameClient client, String data) {
        String[] parameter = data.split(",");

        if (parameter.length > 2 && !checkAccess(client, Integer.parseInt(parameter[2])))
            return;

        client.getPlayer().changeMap(Integer.parseInt(parameter[0]), Short.parseShort(parameter[1]));
    }

    private boolean checkAccess(GameClient client, int restrictedGameMap) {
        return client.getPlayer().getGameMap().getId() == restrictedGameMap;
    }
}
