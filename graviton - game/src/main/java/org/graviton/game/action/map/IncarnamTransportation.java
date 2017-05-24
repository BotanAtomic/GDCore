package org.graviton.game.action.map;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.game.breeds.AbstractBreed;
import org.graviton.game.client.player.Player;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;

/**
 * Created by Botan on 09/12/2016. 09:23
 */

@GameAction(id=183)
public class IncarnamTransportation implements Action {
    @Override
    public void apply(GameClient client, Object data) {
        Player player = client.getPlayer();

        if (player.getLevel() > 15) {
            player.send(MessageFormatter.insufficientLevelForIncarnam());
        } else {
            client.send(MessageFormatter.savedPositionMessage());

            AbstractBreed breed = client.getPlayer().getBreed();
            client.getPlayer().changeMap(breed.incarnamMap(), breed.incarnamCell());
            client.getPlayer().setSavedLocation(client.getPlayer().getLocation().copy());
        }

        client.getInteractionManager().removeFirst();
    }

    @Override
    public void finish() {

    }
}
