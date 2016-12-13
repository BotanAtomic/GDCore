package org.graviton.game.fight.type;

import org.graviton.game.client.player.Player;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.fight.common.FightType;
import org.graviton.game.fight.team.FightTeam;
import org.graviton.game.maps.GameMap;

/**
 * Created by Botan on 10/12/2016. 21:47
 */
public class DuelFight extends Fight {

    public DuelFight(int id, Player first, Player second, GameMap gameMap) {
        super(id, new FightTeam(first, FightSide.RED), new FightTeam(second, FightSide.BLUE), gameMap);
    }

    @Override
    protected byte scheduledTime() {
        return 0;
    }

    @Override
    protected byte canQuit() {
        return 1;
    }

    @Override
    protected FightType getType() {
        return FightType.DUEL;
    }
}
