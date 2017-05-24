package org.graviton.game.action.map;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.game.interaction.InteractionType;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.trunk.type.Trunk;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;

import static org.graviton.network.game.protocol.HousePacketFormatter.houseCodeMessage;

/**
 * Created by Botan on 24/05/17. 12:17
 */

@GameAction(id=104)
public class OpenPrivateTrunk implements Action {

    @Override
    public void apply(GameClient client, Object data) {
        Trunk trunk = client.getPlayer().getGameMap().getTrunks().get(((Cell) data).getId());

        if (trunk.inUse())
            client.send(MessageFormatter.trunkInUseMessage());
        else
            client.send(houseCodeMessage());

        client.getInteractionManager().setTrunkInteraction(trunk);
        client.getPlayer().getGameMap().send(GamePacketFormatter.interactiveObjectActionMessage(InteractionType.MAP_ACTION.getId(), client.getPlayer().getId(), ((Cell) data).getId(), 0));
    }

    @Override
    public void finish() {

    }
}