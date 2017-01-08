package org.graviton.game.effect.type.buff;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.type.SpellBoostBuff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;

import java.util.Collection;

/**
 * Created by Botan on 01/01/2017. 22:10
 */
public class SpellBoostEffect implements Effect {
    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        new SpellBoostBuff(fighter, fighter.getCreature().entityFactory().getSpellTemplate(effect.getFirst()), effect, (short) (effect.getTurns() + 1));
    }
}
