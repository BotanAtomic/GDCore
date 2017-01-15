package org.graviton.game.interaction.actions;

import org.graviton.game.client.player.Player;
import org.graviton.game.interaction.AbstractGameAction;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;


/**
 * Created by Botan on 28/12/2016. 21:24
 */
public class AskDefy implements AbstractGameAction {

    public AskDefy(GameClient client, int targetId) {
        Player target = client.getPlayerRepository().find(targetId);

        if (target == null) {
            client.send(GamePacketFormatter.awayPlayerMessage(targetId));
            return;
        }

        client.getInteractionManager().setInteractionWith(targetId);
        target.getAccount().getClient().getInteractionManager().setInteractionWith(client.getPlayer().getId());
        client.getPlayer().getMap().send(GamePacketFormatter.askDuelMessage(client.getPlayer().getId(), targetId));
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
