package org.graviton.game.intelligence.artificial;

import org.graviton.game.fight.Fighter;
import org.graviton.game.intelligence.ArtificialIntelligence;

/**
 * Created by Botan on 18/01/2017. 23:30
 */
public class PassIntelligence extends ArtificialIntelligence {

    public PassIntelligence(Fighter fighter) {
        super(fighter);
    }

    @Override
    public void run() {
        this.fightTurn.end();
    }
}
