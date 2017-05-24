package org.graviton.game.action.map;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.game.interaction.InteractionType;
import org.graviton.game.maps.cell.Cell;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.HousePacketFormatter;

/**
 * Created by Botan on 25/03/2017. 21:04
 */

@GameAction(id=81)
public class LockHouse implements Action {

    @Override
    public void apply(GameClient client, Object data) {
        if (data != null)
            client.getInteractionManager().setHouseInteraction(client.getPlayer().getGameMap().getHouses().get(((Cell) data).getId()));
        client.send(HousePacketFormatter.houseLockCodeMessage());
        client.getPlayer().getGameMap().send(GamePacketFormatter.interactiveObjectActionMessage(InteractionType.MAP_ACTION.getId(), client.getPlayer().getId(), ((Cell) data).getId(), 0));
    }

    @Override
    public void finish() {

    }
}
