package org.graviton.game.effect.type.buff;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.type.PunishmentBuff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;

import java.util.Collection;

/**
 * Created by Botan on 07/01/2017. 12:39
 */
public class PunishmentEffect implements Effect {
    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        new PunishmentBuff(fighter, effect);
        System.err.println(fighter.getBuffs(PunishmentBuff.class).size() + " for " + effect.getTurns());
    }
}
