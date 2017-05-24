package org.graviton.game.action.map;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.trunk.type.Trunk;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.MessageFormatter;

/**
 * Created by Botan on 21/05/17. 11:20
 */

@GameAction(id = 153)
public class OpenTrunk implements Action {

    @Override
    public void apply(GameClient client, Object data) {
        Trunk trunk = client.getPlayer().getGameMap().getTrunks().get(((Cell) data).getId());

        if (trunk.inUse())
            client.send(MessageFormatter.trunkInUseMessage());
        else
            trunk.open(client.getPlayer(), "-");

    }

    @Override
    public void finish() {

    }
}
