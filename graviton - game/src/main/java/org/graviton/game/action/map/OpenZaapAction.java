package org.graviton.game.action.map;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.game.client.player.Player;
import org.graviton.game.interaction.InteractionType;
import org.graviton.game.maps.cell.Cell;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

/**
 * Created by Botan on 26/03/2017. 17:38
 */

@GameAction(id = 114)
public class OpenZaapAction implements Action {
    @Override
    public void apply(GameClient client, Object data) {
        Player player = client.getPlayer();

        if (player.getAlignment().getDishonor() > 2)
            player.send(MessageFormatter.notPermittedDishonorMessage());
        else
            player.send(PlayerPacketFormatter.zaapListMessage(player.getZaaps(), player.getGameMap()));

        client.getPlayer().getGameMap().send(GamePacketFormatter.interactiveObjectActionMessage(InteractionType.MAP_ACTION.getId(), client.getPlayer().getId(), ((Cell) data).getId(), 0));
    }

    @Override
    public void finish() {

    }
}
