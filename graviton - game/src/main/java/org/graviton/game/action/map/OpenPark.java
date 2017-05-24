package org.graviton.game.action.map;

import org.graviton.game.action.Action;
import org.graviton.game.client.player.Player;
import org.graviton.game.mountpark.MountPark;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.MessageFormatter;


/**
 * Created by Botan on 07/05/17. 23:47
 */
public class OpenPark implements Action {

    @Override
    public void apply(GameClient client, Object data) {
        Player player = client.getPlayer();
        MountPark park = client.getPlayer().getGameMap().getMountPark();

        if(player.getAlignment().getDishonor() > 5) {
            player.send(MessageFormatter.notPermittedDishonorMessage());
            return;
        }



    }

    @Override
    public void finish() {

    }

}
