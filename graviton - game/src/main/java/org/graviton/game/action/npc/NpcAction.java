package org.graviton.game.action.npc;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.action.Action;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 08/12/2016. 17:32
 */

@Slf4j
public enum NpcAction {
    TRANSPORTATION((short) 0, Transportation.class),
    DIALOG((short) 1, Dialog.class),
    QUEST((short) 40, Quest.class),
    ASTRUB_TRANSPORTATION((short) 229, AstrubTransportation.class),
    FINISH_QUEST((short) 984, FinishQuest.class);

    private final short id;
    private final Class<?> actionClass;

    NpcAction(short id, Class<?> actionClass) {
        this.id = id;
        this.actionClass = actionClass;
    }

    public static NpcAction get(short id) {
        for (NpcAction value : values())
            if (value.id == id)
                return value;
        return null;
    }

    public void apply(GameClient client, String data) {
        try {
            ((Action) actionClass.newInstance()).apply(client, data);
        } catch (Exception e) {
            log.error("exception > {}", e.getMessage());
        }
    }
}
