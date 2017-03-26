package org.graviton.game.action.common;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.action.Action;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 10/12/2016. 13:10
 */

@Slf4j
public class Performer {

    public static Action apply(GameClient client, Class<?> actionClass, Object data) {
        try {
            Action action = ((Action) actionClass.newInstance());
            action.apply(client, data);
            return action;
        } catch (Exception e) {
            log.error("Performer exception > {}", e.getMessage());
        }
        return null;
    }
}
