package org.graviton.game.intelligence.artificial;

import org.graviton.game.fight.Fighter;
import org.graviton.game.intelligence.api.ArtificialIntelligence;
import org.graviton.game.intelligence.api.Intelligence;
import org.graviton.utils.Cells;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Botan on 08/04/2017. 22:42
 */
@Intelligence(value = 32, repetition = 4)
public class BaseAutoBuffIntelligence extends ArtificialIntelligence {

    public BaseAutoBuffIntelligence(Fighter fighter) {
        super(fighter);
    }

    @Override
    public short run() {
        AtomicInteger time = new AtomicInteger(100);
        AtomicInteger bestRange = new AtomicInteger(1);

        boolean onAction = false;

        Fighter predicateTarget = getNearestEnemy(fighter.getFight(), fighter);

        fighter.getSpells().forEach(spell -> bestRange.set(spell.getMaximumRange() > bestRange.get() ? spell.getMaximumRange() : bestRange.get()));

        Fighter longestEnemy = getNearestEnemy(fighter.getFight(), this.fighter, (byte) 0, (byte) (bestRange.get() + 1));

        byte movementLimit = 0;

        if (predicateTarget != null && longestEnemy == null) {
            int distance = Cells.distanceBetween(fighter.getFight().getFightMap().getWidth(), fighter.getFightCell().getId(), predicateTarget.getFightCell().getId());
            movementLimit = (byte) (distance - bestRange.get());
        }

        if (fighter.getCurrentActionPoint() > 0) {
            if (tryToInvoke(fighter.getFight(), fighter, fighter.getSpellFilter().getInvocation(), false)) {
                time.set((short) (time.get() + 2200));
                onAction = true;
            }
        }

        if (!onAction && fighter.getCurrentActionPoint() > 0) {
            if (tryToBuff(fighter, fighter)) {
                time.set((short) (time.get() + 1000));
                onAction = true;
            }
        }


        if (fighter.getCurrentMovementPoint() > 0 && longestEnemy == null && !this.attack && !onAction) {
            time.set(tryToMove(fighter.getFight(), fighter, predicateTarget, true, movementLimit));

            if (time.get() != 0) {
                onAction = true;
                longestEnemy = getNearestEnemy(fighter.getFight(), this.fighter, (byte) 0, (byte) (bestRange.get() + 1));
            }
        }


        if (!onAction && fighter.getCurrentActionPoint() > 0) {
            if (tryToBuff(fighter, fighter)) {
                time.set((short) (time.get() + 1000));
                onAction = true;
            }
        }

        if (fighter.getCurrentActionPoint() > 0 && longestEnemy != null && !onAction) {
            short attackResult = tryToAttack(fighter.getFight(), fighter, fighter.getSpellFilter().getAttack());

            if (attackResult != 0) {
                time.set((short) (time.get() + attackResult));
                onAction = true;
                this.attack = true;
            }
        }

        if (fighter.getCurrentMovementPoint() > 0 && this.attack && !onAction)
            time.set((short) (time.get() + move(fighter, Cells.moreFarCell(fighter), (byte) 0)));

        System.err.println("BAB break");
        return time.shortValue();
    }


}
