package org.graviton.game.action.map;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.game.interaction.InteractionType;
import org.graviton.game.maps.cell.Cell;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;

/**
 * Created by Botan on 23/03/2017. 22:11
 */

@GameAction(id=44)
public class SaveAction implements Action{

    @Override
    public void apply(GameClient client, Object data) {
        client.getPlayer().setSavedLocation(client.getPlayer().getLocation().copy());
        client.send(MessageFormatter.savedPositionMessage());
        client.getPlayer().update();
        client.getPlayer().getGameMap().send(GamePacketFormatter.interactiveObjectActionMessage(InteractionType.MAP_ACTION.getId(), client.getPlayer().getId(), ((Cell) data).getId(), 0));
    }

    @Override
    public void finish() {

    }
}
