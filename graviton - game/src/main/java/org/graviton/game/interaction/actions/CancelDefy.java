package org.graviton.game.interaction.actions;

import org.graviton.game.client.player.Player;
import org.graviton.game.interaction.AbstractGameAction;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;

/**
 * Created by Botan on 28/12/2016. 21:34
 */
public class CancelDefy implements AbstractGameAction {

    public CancelDefy(GameClient client, int targetId) {
        Player target = client.getPlayerRepository().get(targetId);

        if (target == null)
            client.send(GamePacketFormatter.awayPlayerMessage(targetId));

        client.getPlayer().getMap().send(GamePacketFormatter.cancelDuelMessage(client.getPlayer().getId(), target.getId()));

        client.getInteractionManager().setInteractionWith(0);
        target.getAccount().getClient().getInteractionManager().setInteractionWith(0);
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
