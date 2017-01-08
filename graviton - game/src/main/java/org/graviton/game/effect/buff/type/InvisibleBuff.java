package org.graviton.game.effect.buff.type;

import org.graviton.game.effect.buff.Buff;
import org.graviton.game.fight.Fighter;

/**
 * Created by Botan on 29/12/2016. 19:57
 */
public class InvisibleBuff extends Buff {

    public InvisibleBuff(Fighter fighter, short remainingTurn) {
        super(fighter, remainingTurn);
        this.fighter = fighter;

        fighter.setVisible(false, remainingTurns);
    }

    @Override
    public void destroy() {
        fighter.setVisible(true, (short) 0);
    }

    @Override
    public void check() {

    }

}
