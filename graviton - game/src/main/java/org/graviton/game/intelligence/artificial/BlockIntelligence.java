package org.graviton.game.intelligence.artificial;

import org.graviton.game.fight.Fighter;
import org.graviton.game.intelligence.api.ArtificialIntelligence;
import org.graviton.game.intelligence.api.Intelligence;

/**
 * Created by Botan on 12/04/2017. 15:04
 */

@Intelligence(value = 5, repetition = 1)
public class BlockIntelligence extends ArtificialIntelligence {

    public BlockIntelligence(Fighter fighter) {
        super(fighter);
    }

    @Override
    public short run() {
        short time = 0;

        Fighter predicateTarget = getNearestEnemy(fighter.getFight(), fighter);

        if (fighter.getCurrentMovementPoint() > 0 && predicateTarget != null)
            time = tryToMove(fighter.getFight(), fighter, predicateTarget, false, (byte) 0);


        return time;
    }
}
