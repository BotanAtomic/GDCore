package org.graviton.game.action.map;

import org.graviton.game.action.Action;
import org.graviton.game.house.House;
import org.graviton.game.interaction.InteractionType;
import org.graviton.game.maps.cell.Cell;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;

import static org.graviton.network.game.protocol.HousePacketFormatter.houseCodeMessage;

/**
 * Created by Botan on 25/03/2017. 18:45
 */
public class EnterHouseAction implements Action {
    @Override
    public void apply(GameClient client, Object data) {
        House house = data != null ? client.getPlayer().getGameMap().getHouses().get(((Cell) data).getId()) : client.getInteractionManager().getHouseInteraction();

        if (house.getOwner() == 0 || house.getOwner() == client.getAccount().getId())
            house.open(client.getPlayer(), "-");
        else
            client.send(houseCodeMessage());

        client.getInteractionManager().setHouseInteraction(house);
        client.getPlayer().getGameMap().send(GamePacketFormatter.interactiveObjectActionMessage(InteractionType.MAP_ACTION.getId(), client.getPlayer().getId(), ((Cell) data).getId(), 0));
    }

    @Override
    public void finish() {

    }
}
