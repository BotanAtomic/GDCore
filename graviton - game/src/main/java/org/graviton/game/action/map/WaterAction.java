package org.graviton.game.action.map;

import org.graviton.game.action.Action;
import org.graviton.game.client.player.Player;
import org.graviton.game.interaction.InteractionType;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.object.InteractiveObjectState;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;

import static org.graviton.constant.Dofus.WATER_OBJECT;

/**
 * Created by Botan on 22/03/2017. 22:11
 */
public class WaterAction implements Action {
    private Cell cell;
    private Player player;

    @Override
    public void apply(GameClient client, Object data) {
        this.player = client.getPlayer();
        Cell cell = (this.cell = (Cell) data);
        cell.getInteractiveObject().use();
        player.getGameMap().send(GamePacketFormatter.interactiveObjectActionMessage(InteractionType.MAP_ACTION.getId(), client.getPlayer().getId(), cell.getId(), cell.getInteractiveObject().getTemplate().getDuration()));
    }

    @Override
    public void finish() {
        this.cell.getInteractiveObject().setState(InteractiveObjectState.EMPTY);
        final byte quantity = (byte) (Math.random() * 9 + 1);
        player.getGameMap().send(cell.getInteractiveObject().getData());
        player.getGameMap().send(GamePacketFormatter.quantityAnimationMessage(player.getId(), quantity));
        player.getInventory().addItem(player.getEntityFactory().getItemTemplate(WATER_OBJECT).createRandom(player.getEntityFactory().getNextItemId()), true);
    }
}
