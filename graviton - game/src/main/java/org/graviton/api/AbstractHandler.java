package org.graviton.api;

import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 05/11/2016 : 12:27
 */
public interface AbstractHandler {

    void apply(GameClient client, String data, String header);

}
