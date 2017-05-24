package org.graviton.game.intelligence.artificial;

import org.graviton.game.fight.Fighter;
import org.graviton.game.intelligence.api.ArtificialIntelligence;
import org.graviton.game.intelligence.api.Intelligence;
import org.graviton.utils.Cells;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Botan on 18/01/2017. 23:30
 */

@Intelligence(value = 9, repetition = 4)
public class DeceitfulIntelligence extends ArtificialIntelligence {

    public DeceitfulIntelligence(Fighter fighter) {
        super(fighter);
    }

    @Override
    public short run() {
        AtomicInteger time = new AtomicInteger(100);

        AtomicInteger bestRange = new AtomicInteger(1);
        fighter.getSpells().forEach(spell -> bestRange.set(spell.getMaximumRange() > bestRange.get() ? spell.getMaximumRange() : bestRange.get()));

        Fighter predicateTarget = getNearestEnemy(fighter.getFight(), fighter);
        Fighter target = getNearestEnemy(fighter.getFight(), fighter, (byte) 0, bestRange.byteValue());

        boolean onAction = false;

        byte movementLimit = 0;
        if (predicateTarget != null && target == null) {
            int distance = Cells.distanceBetween(fighter.getFight().getFightMap().getWidth(), fighter.getFightCell().getId(), predicateTarget.getFightCell().getId());
            movementLimit = (byte) (distance - bestRange.get());
        }

        if (fighter.getCurrentMovementPoint() > 0 && target == null) {
            time.set((short) (time.get() + tryToMove(fighter.getFight(), fighter, predicateTarget, false, movementLimit)));
            if (time.get() != 100)
                onAction = true;
        }

        if (fighter.getCurrentActionPoint() > 0 && target != null) {
            short attackResult = tryToAttack(fighter.getFight(), fighter, fighter.getSpellFilter().getAttack());

            if (attackResult != 0) {
                time.set((short) (time.get() + attackResult));
                onAction = true;
            }
        }

        if (fighter.getCurrentMovementPoint() > 0 && !onAction)
            time.set((short) (time.get() + move(fighter, Cells.moreFarCell(fighter), (byte) 0)));

        return time.shortValue();
    }
}
