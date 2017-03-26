package org.graviton.game.action;

import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 08/12/2016. 17:21
 */
public interface Action {

    void apply(GameClient client, Object data);

    void finish();

}
