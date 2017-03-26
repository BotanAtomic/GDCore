package org.graviton.game.effect.type.other;

import org.graviton.game.creature.monster.extra.Double;
import org.graviton.game.effect.Effect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;

import java.util.Collection;

/**
 * Created by Botan on 09/02/2017. 18:59
 */
public class DoubleEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        InvocationEffect.invokeDouble(fighter, new Double(fighter, fighter.getFight().nextId()), selectedCell);
    }

    @Override
    public Effect copy() {
        return new DoubleEffect();
    }
}
