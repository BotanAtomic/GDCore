package org.graviton.game.intelligence.artificial;

import org.graviton.game.fight.Fighter;
import org.graviton.game.intelligence.ArtificialIntelligence;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Botan on 03/03/2017. 21:04
 */
public class AttackIntelligence extends ArtificialIntelligence {

    public AttackIntelligence(Fighter fighter) {
        super(fighter);
    }

    @Override
    public void run() {
        AtomicReference<Short> time = new AtomicReference<>((short) 100);
        boolean onAction = false;

        AtomicInteger bestRange = new AtomicInteger(1);

        Fighter predicateTarget = getNearestEnemy(fighter.getFight(), fighter);

        fighter.getSpells().forEach(spell -> bestRange.set(spell.getMaximumRange() > bestRange.get() ? spell.getMaximumRange() : bestRange.get()));

        Fighter firstTarget = getNearestEnemy(fighter.getFight(), fighter, (byte) 1, (byte) (bestRange.get() + 1));
        Fighter secondTarget = getNearestEnemy(fighter.getFight(), fighter, (byte) 0, (byte) 2);

        if (bestRange.get() == 1)
            firstTarget = null;

        if (firstTarget != null && !firstTarget.isVisible())
            firstTarget = null;

        if (secondTarget != null && !secondTarget.isVisible())
            secondTarget = null;

        if (fighter.getCurrentMovementPoint() > 0 && firstTarget == null && secondTarget == null) {
            time.set((short) (time.get() + tryToMove(fighter.getFight(), fighter, predicateTarget, false)));
            if (time.get() != 100) {
                firstTarget = getNearestEnemy(fighter.getFight(), fighter, (byte) 1, (byte) (bestRange.get() + 1));
                secondTarget = getNearestEnemy(fighter.getFight(), fighter, (byte) 0, (byte) 2);
                onAction = true;

                if (bestRange.get() == 1)
                    firstTarget = null;
            }
        }

        if (!onAction && fighter.getCurrentActionPoint() > 0) {
            if (tryToInvoke(fighter.getFight(), fighter, fighter.getSpellFilter().getInvocation())) {
                time.set((short) (time.get() + 2200));
            } else if (firstTarget != null && secondTarget == null) {
                time.set((short) (200 + time.get() + tryToAttack(fighter.getFight(), fighter, fighter.getSpellFilter().getAttack())));
            } else if (secondTarget != null) {
                short attack = tryToAttack(fighter.getFight(), fighter, fighter.getSpellFilter().getBest());
                if (attack == 0)
                    attack = tryToAttack(fighter.getFight(), fighter, fighter.getSpellFilter().getAttack());
                time.set((short) (200 + time.get() + attack));
            }
        }


        if (fighter.getCurrentMovementPoint() > 0)
            time.set((short) (time.get() + tryToMove(fighter.getFight(), fighter, firstTarget == null ? secondTarget == null ? predicateTarget : secondTarget : firstTarget, false)));

        this.count++;

        if (fighter.getCurrentMovementPoint() == 0 && fighter.getCurrentActionPoint() == 0 || count == 4)
            fighter.getFight().schedule(() -> fighter.getTurn().end(true), time.get());
        else
            fighter.getFight().schedule(this::run, time.get());
    }
}
