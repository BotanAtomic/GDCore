package org.graviton.game.effect.type.other;

import org.graviton.game.effect.Effect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;

import java.util.Collection;

/**
 * Created by Botan on 30/12/2016. 03:27
 */
public class VisibleEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.stream().filter(target -> !target.isVisible()).forEach(target -> target.setVisible(true, (short) 0));
    }

}
