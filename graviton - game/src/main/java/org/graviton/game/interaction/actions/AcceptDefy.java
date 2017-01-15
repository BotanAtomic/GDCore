package org.graviton.game.interaction.actions;

import org.graviton.game.client.player.Player;
import org.graviton.game.interaction.AbstractGameAction;
import org.graviton.game.maps.GameMap;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;

/**
 * Created by Botan on 28/12/2016. 21:29
 */
public class AcceptDefy implements AbstractGameAction {

    public AcceptDefy(GameClient client, int targetId, GameMap gameMap) {
        Player target = client.getPlayerRepository().find(targetId);

        if (target == null) {
            client.send(GamePacketFormatter.awayPlayerMessage(targetId));
            return;
        }

        gameMap.send(GamePacketFormatter.acceptDuelMessage(client.getPlayer().getId(), target.getId()));

        gameMap.getFightFactory().newDuel(target, client.getPlayer());
    }

    @Override
    public boolean begin() {
        return false;
    }

    @Override
    public void cancel(String data) {

    }

    @Override
    public void finish(String data) {

    }
}
