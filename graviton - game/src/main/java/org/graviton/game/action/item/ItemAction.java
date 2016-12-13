package org.graviton.game.action.item;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.action.common.Performer;
import org.graviton.game.action.common.Transportation;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 10/12/2016. 12:59
 */

@Slf4j
public enum ItemAction {
    TRANSPORTATION((byte) 0, Transportation.class);

    private final byte id;
    private final Class<?> actionClass;

    ItemAction(byte id, Class<?> actionClass) {
        this.id = id;
        this.actionClass = actionClass;
    }

    public static ItemAction get(byte id) {
        for (ItemAction value : values())
            if (value.id == id)
                return value;
        return null;
    }

    public void apply(GameClient client, String data) {
        Performer.apply(client, actionClass, data);
    }
}
