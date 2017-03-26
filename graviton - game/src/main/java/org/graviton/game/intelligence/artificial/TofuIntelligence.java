package org.graviton.game.intelligence.artificial;

import org.graviton.game.fight.Fighter;
import org.graviton.game.intelligence.ArtificialIntelligence;
import org.graviton.utils.Cells;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Botan on 18/03/2017. 23:32
 */
public class TofuIntelligence extends ArtificialIntelligence {
    private boolean attack;

    public TofuIntelligence(Fighter fighter) {
        super(fighter);
    }

    @Override
    public void run() {
        AtomicReference<Short> time = new AtomicReference<>((short) 100);
        AtomicInteger bestRange = new AtomicInteger(1);
        Fighter predicateTarget = getNearestEnemy(fighter.getFight(), fighter);

        boolean onAction = false;

        byte actionPoint = fighter.getCurrentActionPoint(), movementPoint = fighter.getCurrentMovementPoint();

        fighter.getSpells().forEach(spell -> bestRange.set(spell.getMaximumRange() > bestRange.get() ? spell.getMaximumRange() : bestRange.get()));

        Fighter target = getNearestEnemy(fighter.getFight(), fighter, (byte) 0, (byte) 3);

        if (target != null && !target.isVisible())
            target = null;

        if (movementPoint > 0 && target == null && actionPoint > 0) {
            time.set((short) (time.get() + tryToMove(fighter.getFight(), fighter, predicateTarget, true)));
            if (time.get() != 100) {
                target = getNearestEnemy(fighter.getFight(), fighter, (byte) 0, (byte) 3);
                onAction = true;
            }
        }

        actionPoint = this.fighter.getCurrentActionPoint();
        movementPoint = this.fighter.getCurrentMovementPoint();

        if (actionPoint > 0 && target != null && !onAction) {
            time.set((short) (time.get() + tryToAttack(this.fighter.getFight(), this.fighter, fighter.getSpells())));
            if (time.get() != 100) {
                this.attack = true;
                onAction = true;
            }
        }

        if (movementPoint > 0 && this.attack && !onAction) {
            time.set((short) (time.get() + move(fighter, Cells.moreFarCell(fighter))));
        }
        this.count++;

        if (fighter.getCurrentMovementPoint() == 0 && fighter.getCurrentActionPoint() == 0 || count == 4)
            fighter.getFight().schedule(() -> fighter.getTurn().end(true), time.get());
        else
            fighter.getFight().schedule(this::run, time.get());
    }
}
