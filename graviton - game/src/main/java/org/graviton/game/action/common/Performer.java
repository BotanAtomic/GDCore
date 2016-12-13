package org.graviton.game.action.common;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.action.Action;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 10/12/2016. 13:10
 */

@Slf4j
public class Performer {

    public static void apply(GameClient client, Class<?> actionClass, String data) {
        try {
            ((Action) actionClass.newInstance()).apply(client, data);
        } catch (Exception e) {
            log.error("exception > {}", e.getMessage());
        }
    }
}
