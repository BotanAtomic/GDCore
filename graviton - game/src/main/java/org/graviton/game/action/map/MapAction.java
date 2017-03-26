package org.graviton.game.action.map;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.Performer;
import org.graviton.game.maps.cell.Cell;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 22/03/2017. 19:39
 */
public enum MapAction {
    POTATOES((short) 42, null),
    WATER((short) 102, WaterAction.class),
    FOUNTAIN_OF_YOUTH((short) 62, YouthFountainAction.class),
    SAVE((short) 44, SaveAction.class),
    INCARNAM_TRANSPORTATION((short) 183, IncarnamTransportation.class),

    ZAAP((short) 114, OpenZaapAction.class),
    ZAAPI((short) 157, null),

    ENCLOSURES_ACCESS((short) 175, null),
    ENCLOSURES_BUY((short) 176, null),
    ENCLOSURES_SELL((short) 177, null),
    ENCLOSURES_MODIFY((short) 178, null),

    HOME_LOCK((short) 81, LockHouseAction.class),
    HOME_ENTER((short) 84, EnterHouseAction.class),
    HOME_BUY((short) 97, BuyHouseAction.class),
    HOME_SELL((short) 98, BuyHouseAction.class),
    HOME_MODIFY((short) 108, BuyHouseAction.class),

    PRIVATE_TRUNK_OPEN((short) 104, null),
    TRUNK_LOCK((short) 105, null),
    TRUNK_OPEN((short) 153, null),
    TRUNK_SELL((short) 98, null),

    ARTISANS_BOOK((short) 170, null),
    OBJECT_BROKE((short) 181, null);

    private final short id;
    private final Class<?> actionClass;

    MapAction(short id, Class<?> action) {
        this.id = id;
        this.actionClass = action;
    }

    public static MapAction get(short id) {
        for (MapAction value : values())
            if (value.id == id)
                return value;
        return null;
    }

    public Action apply(GameClient client, Cell cell) {
        return Performer.apply(client, actionClass, cell);
    }


}
