package org.graviton.game.effect.type.other;

import org.graviton.game.effect.Effect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;

import java.util.Collection;

/**
 * Created by Botan on 05/01/2017. 17:26
 */
public class LifeTransferEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        double factor = effect.getFirst() / 100;
        int toGive = (int) (fighter.getLife().getCurrent() * factor);

        targets.forEach(target -> target.getFight().hit(fighter, target, toGive * -1));
        fighter.getFight().hit(fighter, fighter, toGive);
    }
}
