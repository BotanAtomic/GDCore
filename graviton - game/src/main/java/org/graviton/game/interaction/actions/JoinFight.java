package org.graviton.game.interaction.actions;

import org.graviton.game.client.player.Player;
import org.graviton.game.fight.Fight;
import org.graviton.game.interaction.AbstractGameAction;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 28/12/2016. 21:36
 */
public class JoinFight implements AbstractGameAction {

    public JoinFight(GameClient client, String[] data) {
        if (data.length == 1) {
            int fight = Integer.parseInt(data[0]);

            // TODO: 19/12/2016 join as spectator
        } else {
            Player target = client.getPlayerRepository().get(Integer.parseInt(data[1]));

            if (target != null) {
                Fight fight = target.getFight();
                fight.join(target.getTeam(), client.getPlayer());
            }
        }
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
