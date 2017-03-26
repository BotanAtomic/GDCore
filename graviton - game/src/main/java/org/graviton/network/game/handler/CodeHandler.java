package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.house.House;
import org.graviton.network.game.GameClient;

import static org.graviton.network.game.protocol.HousePacketFormatter.quitHouseCodeMessage;

/**
 * Created by Botan on 26/03/2017. 00:29
 */

@Slf4j
public class CodeHandler {
    private final GameClient client;

    public CodeHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, char subHeader) {
        switch (subHeader) {
            case 'K':
                receiveCode(data);
                break;

            case 'V':
                client.send(quitHouseCodeMessage());
                client.getInteractionManager().setHouseInteraction(null);
                break;

            default:
                log.error("not implemented code packet '{}'", subHeader);
        }
    }

    private void receiveCode(String data) {
        switch (data.charAt(0)) {
            case '0' :
                if(client.getInteractionManager().getHouseInteraction() != null)
                    client.getInteractionManager().getHouseInteraction().open(client.getPlayer(), data.substring(2));
                break;

            case '1':
                House house = client.getInteractionManager().getHouseInteraction();
                if(house != null && house.getOwner() == client.getAccount().getId()) {
                    house.setKey(data.substring(2));
                    client.getEntityFactory().updateHouse(house);
                }
                break;
        }
        client.send(quitHouseCodeMessage());
    }
}
