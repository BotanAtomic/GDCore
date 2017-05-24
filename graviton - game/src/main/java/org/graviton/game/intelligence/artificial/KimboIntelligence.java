package org.graviton.game.intelligence.artificial;

import org.graviton.game.fight.Fighter;
import org.graviton.game.intelligence.api.ArtificialIntelligence;
import org.graviton.game.intelligence.api.Intelligence;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Botan on 18/01/2017. 23:30
 */
@Intelligence(value = 17, repetition = 4)
public class KimboIntelligence extends ArtificialIntelligence { //TODO : later : very complex

    public KimboIntelligence(Fighter fighter) {
        super(fighter);
    }

    @Override
    public short run() {
        Fighter predicateTarget = getNearestEnemy(fighter.getFight(), fighter);

        AtomicInteger time = new AtomicInteger(100);

        boolean onAction = false;

        AtomicInteger bestRange = new AtomicInteger(1);
        fighter.getSpells().forEach(spell -> bestRange.set(spell.getMaximumRange() > bestRange.get() ? spell.getMaximumRange() : bestRange.get()));

        Fighter target = getNearestEnemy(fighter.getFight(), this.fighter, (byte) 0, (byte) 2);

        if (fighter.getCurrentActionPoint() > 0) {
            if (tryToInvoke(fighter.getFight(), fighter, fighter.getSpellFilter().getInvocation(), true)) {
                time.set((short) (time.get() + 2200));
                onAction = true;
            }
        }

        if (fighter.getCurrentMovementPoint() > 0 && target == null && predicateTarget != null) {
            time.set((short) (time.get() + tryToMove(fighter.getFight(), fighter, predicateTarget, false, (byte) 0)));
            if (time.get() != 100)
                onAction = true;
        }

        if (this.fighter.getCurrentActionPoint() > 0 && target == null && predicateTarget != null && !onAction) {
            short result = tryToJump(fighter, predicateTarget);
            if (result != 0) {
                onAction = true;
                time.addAndGet(result);
            }
        }

        if (fighter.getCurrentActionPoint() > 0 && target != null && !onAction) {
            short attack = tryToAttack(fighter.getFight(), fighter, fighter.getSpellFilter().getBest());
            if (attack == 0)
                attack = tryToAttack(fighter.getFight(), fighter, fighter.getSpellFilter().getAttack());
            time.addAndGet(attack);

        }

        return time.shortValue();
    }
}
