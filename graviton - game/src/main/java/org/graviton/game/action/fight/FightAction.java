package org.graviton.game.action.fight;

import org.graviton.game.action.common.Performer;
import org.graviton.game.action.common.Transportation;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 26/03/2017. 14:12
 */
public enum FightAction {
    TRANSPORTATION((short) 0, Transportation.class);

    private final short id;
    private final Class<?> actionClass;

    FightAction(short id, Class<?> actionClass) {
        this.id = id;
        this.actionClass = actionClass;
    }

    public static FightAction get(short id) {
        for (FightAction value : values())
            if (value.id == id)
                return value;
        return null;
    }

    public void apply(GameClient client, String data) {
        Performer.apply(client, actionClass, data);
    }
}
