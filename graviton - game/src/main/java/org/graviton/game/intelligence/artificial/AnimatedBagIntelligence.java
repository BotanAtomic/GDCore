package org.graviton.game.intelligence.artificial;

import org.graviton.game.fight.Fighter;
import org.graviton.game.intelligence.api.ArtificialIntelligence;
import org.graviton.game.intelligence.api.Intelligence;
import org.graviton.game.spell.Spell;
import org.graviton.utils.Cells;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Botan on 18/01/2017. 23:30
 */

@Intelligence(value = 24, repetition = 4)
public class AnimatedBagIntelligence extends ArtificialIntelligence {

    public AnimatedBagIntelligence(Fighter fighter) {
        super(fighter);
    }

    @Override
    public short run() {
        Spell sacrifice = fighter.getSpells().stream().findAny().get();

        Fighter master = fighter.getMaster();

        if (master != null) {
            byte movementLimit = (byte) (Cells.distanceBetween(fighter.getFight().getFightMap().getWidth(), fighter.getFightCell().getId(), master.getFightCell().getId()) - sacrifice.getMaximumRange());

            if (fighter.canLaunchSpell(sacrifice, master.getId())) {
                if (movementLimit <= 0 && canCastSpell(fighter, sacrifice, fighter.getFightCell(), master.getFightCell().getId()))
                    sacrifice.applyToFight(fighter, master.getFightCell());
                else return tryToMove(fighter.getFight(), fighter, master, false, movementLimit);
            } else return move(fighter, Cells.moreFarCell(fighter), (byte) 0);


        }

        return 0;
    }
}
