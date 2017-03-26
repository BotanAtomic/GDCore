package org.graviton.game.effect.buff;

import org.graviton.game.fight.Fighter;

/**
 * Created by Botan on 28/12/2016. 23:49
 */
public abstract class Buff {
    public short remainingTurns;
    protected Fighter fighter;

    public Buff(Fighter fighter, short remainingTurns) {
        this.fighter = fighter;
        this.remainingTurns = remainingTurns;
        fighter.addBuff(this);
    }

    public abstract void destroy();

    public abstract void clear();

    public abstract void check();

    public void decrement() {
        remainingTurns--;
        if (remainingTurns <= 0) {
            destroy();
            fighter.removeBuff(this);
        }
    }
}
