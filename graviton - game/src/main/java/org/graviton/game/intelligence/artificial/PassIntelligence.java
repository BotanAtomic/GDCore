package org.graviton.game.intelligence.artificial;

import org.graviton.game.fight.Fighter;
import org.graviton.game.intelligence.api.ArtificialIntelligence;
import org.graviton.game.intelligence.api.Intelligence;

/**
 * Created by Botan on 18/01/2017. 23:30
 */
@Intelligence(value = 0,repetition = 1)
public class PassIntelligence extends ArtificialIntelligence {

    public PassIntelligence(Fighter fighter) {
        super(fighter);
    }

    @Override
    public short run() {
        this.fightTurn.end(true);
        return 0;
    }
}
