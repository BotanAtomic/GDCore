package org.graviton.game.effect.type.buff;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.type.InvisibleBuff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;

import java.util.Collection;


/**
 * Created by Botan on 29/12/2016. 19:33
 */
public class InvisibleEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> new InvisibleBuff(target, effect, effect.getTurns()));
    }

    @Override
    public Effect copy() {
        return new InvisibleEffect();
    }
}

