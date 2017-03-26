package org.graviton.game.interaction.actions;

import org.graviton.game.client.player.Player;
import org.graviton.game.interaction.AbstractGameAction;
import org.graviton.game.spell.Spell;

/**
 * Created by Botan on 28/12/2016. 21:19
 */
public class SpellAttack implements AbstractGameAction {

    public SpellAttack(Player player, short[] data) {
        Spell spell =  player.getSpellView(data[0]).getSpell();
        player.setOnAction(true);
        spell.applyToFight(player, player.getMap().getCells().get(data[1]));
        player.getFight().schedule(() -> player.setOnAction(false), spell.getTemplate().getDuration());
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
