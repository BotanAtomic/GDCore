package org.graviton.game.effect.type.buff;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.type.ReturnSpellBuff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;

import java.util.Collection;

/**
 * Created by Botan on 09/01/2017. 21:13
 */
public class ReturnSpellEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> new ReturnSpellBuff(target, effect));
    }

    @Override
    public Effect copy() {
        return new ReturnSpellEffect();
    }
}
