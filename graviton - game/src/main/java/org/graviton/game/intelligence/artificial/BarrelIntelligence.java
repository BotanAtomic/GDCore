package org.graviton.game.intelligence.artificial;

import org.graviton.game.fight.Fighter;
import org.graviton.game.intelligence.api.ArtificialIntelligence;
import org.graviton.game.intelligence.api.Intelligence;
import org.graviton.utils.Cells;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Botan on 18/01/2017. 23:30
 */

@Intelligence(value = 10, repetition = 8)
public class BarrelIntelligence extends ArtificialIntelligence {

    public BarrelIntelligence(Fighter fighter) {
        super(fighter);
    }

    @Override
    public short run() {
        AtomicInteger time = new AtomicInteger(0);

        Fighter soulTarget = getNearestSoulInlineEnemy(fighter.getFight(), fighter);

        if (fighter.getHoldingBy() != null) {
            time.addAndGet(tryToHeal(fighter, fighter.getHoldingBy()));
        } else if (soulTarget != null) {
            short attack;
            if ((attack = tryToAttack(fighter, soulTarget, fighter.getSpellFilter().getBest())) == 0)
                attack = tryToAttack(fighter, soulTarget, fighter.getSpellFilter().getAttack());

            time.set((short) (400 + time.get() + attack));
        } else {
            getNearestInlineEnemy(fighter).forEach(target -> {
                if (super.attack)
                    return;

                short currentAttack;
                if ((currentAttack = tryToAttack(fighter, target, fighter.getSpellFilter().getBest())) == 0) {
                    if ((currentAttack = tryToAttack(fighter, target, fighter.getSpellFilter().getAttack())) > 0)
                        super.attack = true;
                } else
                    this.attack = true;

                time.set((short) (400 + time.get() + currentAttack));
            });
            this.attack = false;
        }

        return time.shortValue();
    }
}
