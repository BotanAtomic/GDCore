package org.graviton.game.intelligence.artificial;

import org.graviton.game.fight.Fighter;
import org.graviton.game.intelligence.api.ArtificialIntelligence;
import org.graviton.game.intelligence.api.Intelligence;
import org.graviton.utils.Cells;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Botan on 18/01/2017. 23:30
 */

@Intelligence(value = 8, repetition = 4)
public class OverkillIntelligence extends ArtificialIntelligence {

    public OverkillIntelligence(Fighter fighter) {
        super(fighter);
    }

    @Override
    public short run() {
        System.err.println("Run IA 8");
        AtomicInteger time = new AtomicInteger(100);
        AtomicInteger bestRange = new AtomicInteger(1);

        Fighter predicateTarget = getNearestInvocation(fighter.getFight(), fighter, (byte) 0, (byte) 100);

        boolean onAction = false;
        byte actionPoint = fighter.getCurrentActionPoint(), movementPoint = fighter.getCurrentMovementPoint();

        fighter.getSpells().forEach(spell -> bestRange.set(spell.getMaximumRange() > bestRange.get() ? spell.getMaximumRange() : bestRange.get()));

        Fighter target = getNearestInvocation(fighter.getFight(), fighter, (byte) 0, bestRange.byteValue());

        byte movementLimit = 0;

        if (predicateTarget != null && target == null) {
            int distance = Cells.distanceBetween(fighter.getFight().getFightMap().getWidth(), fighter.getFightCell().getId(), predicateTarget.getFightCell().getId());
            movementLimit = (byte) (distance - bestRange.get());
        }

        System.err.println("Target = " + (target == null ? " null" :  target.getName()));

        if (movementPoint > 0 && target == null && actionPoint > 0) {
            time.set((short) (time.get() + tryToMove(fighter.getFight(), fighter, predicateTarget, false, movementLimit)));
            if (time.get() != 100)
                onAction = true;
        }

        actionPoint = fighter.getCurrentActionPoint();
        movementPoint = fighter.getCurrentMovementPoint();


        if (actionPoint > 0 && !onAction) {
            if (tryToInvoke(fighter.getFight(), this.fighter, fighter.getSpellFilter().getInvocation(), true)) {
                time.set((short) (2000 + time.get()));
                onAction = true;
            }
        }

        if (actionPoint > 0 && !onAction && target != null) {
            if (tryToBuff(fighter, target)) {
                time.set((short) (4000 + time.get()));
                onAction = true;
                attack = true;
            }
        }

        if (movementPoint > 0 && !onAction && attack)
            time.set((short) (time.get() + move(fighter, Cells.moreFarCell(fighter), (byte) 0)));

        return time.shortValue();
    }
}
