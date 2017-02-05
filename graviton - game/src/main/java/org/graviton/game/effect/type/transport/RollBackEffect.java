package org.graviton.game.effect.type.transport;

import org.graviton.game.effect.Effect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;

import java.util.Collection;

/**
 * Created by Botan on 21/01/2017. 11:18
 */
public class RollBackEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        fighter.getFight().fighters().stream().filter(target -> target.getStartCell() != null).forEach(target -> new TeleportEffect().apply(target, null, target.getStartCell(), null));
    }

    @Override
    public Effect copy() {
        return new RollBackEffect();
    }
}
