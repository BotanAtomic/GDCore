package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.client.player.Player;
import org.graviton.game.maps.GameMap;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;
import org.graviton.utils.Cells;


/**
 * Created by Botan on 26/03/2017. 17:59
 */

@Slf4j
public class WaypointHandler {
    private final GameClient client;

    public WaypointHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, char subHeader) {
        switch (subHeader) {
            case 'U':
                use(Integer.parseInt(data));
                break;

            case 'V':
                client.send(PlayerPacketFormatter.quitZaapMenuMessage());
                break;

            default:
                log.error("not implemented waypoint packet '{}'", subHeader);
        }
    }

    private void use(int gameMap) {
        Player player = client.getPlayer();
        GameMap newGameMap =  player.getEntityFactory().getMap(gameMap);
        short cost = Cells.getZaapCost(player.getGameMap(), newGameMap);
        player.getInventory().addKamas(cost * -1);

        player.changeMap(newGameMap.getZaap());

        client.send(MessageFormatter.kamasCostMessage(cost));
        client.send(PlayerPacketFormatter.asMessage(player));
        client.send(PlayerPacketFormatter.quitZaapMenuMessage());

    }

}
