package org.graviton.game.interaction.actions;

import org.graviton.game.client.player.Player;
import org.graviton.game.interaction.AbstractGameAction;

/**
 * Created by Botan on 28/12/2016. 21:19
 */
public class SpellAttack implements AbstractGameAction {

    public SpellAttack(Player player, short[] data) {
        player.setOnAction(true);
        player.getSpellView(data[0]).getSpell().applyToFight(player, player.getMap().getCells().get(data[1]));
        player.getFight().schedule(() -> player.setOnAction(false), 1200);
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
