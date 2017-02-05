package org.graviton.game.intelligence;

import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.turn.FightTurn;

/**
 * Created by Botan on 18/01/2017. 23:30
 */
public abstract class ArtificialIntelligence {
    protected final Fighter fighter;
    protected final FightTurn fightTurn;

    public ArtificialIntelligence(Fighter fighter) {
        this.fighter = fighter;
        this.fightTurn = fighter.getTurn();

    }

    public abstract void run();
}
