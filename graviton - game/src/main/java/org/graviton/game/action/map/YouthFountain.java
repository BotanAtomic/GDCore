package org.graviton.game.action.map;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.game.interaction.InteractionType;
import org.graviton.game.maps.cell.Cell;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

/**
 * Created by Botan on 23/03/2017. 20:55
 */

@GameAction(id=62)
public class YouthFountain implements Action {
    private int winLife;
    private GameClient client;

    @Override
    public void apply(GameClient client, Object data) {
        this.client = client;
        this.winLife = client.getPlayer().getLife().getMaximum() - client.getPlayer().getLife().getCurrent();
        client.getPlayer().getGameMap().send(GamePacketFormatter.interactiveObjectActionMessage(InteractionType.MAP_ACTION.getId(), client.getPlayer().getId(), ((Cell) data).getId(), ((Cell) data).getInteractiveObject().getTemplate().getDuration()));
    }

    @Override
    public void finish() {
        client.send(MessageFormatter.regenLifeMessage(client.getPlayer().getLife().getMaximum() - client.getPlayer().getLife().getCurrent()));
        client.getPlayer().getLife().regenMax();
        client.send(GamePacketFormatter.quantityAnimationMessage(client.getPlayer().getId(), winLife));
        client.send(PlayerPacketFormatter.asMessage(client.getPlayer()));
    }
}
