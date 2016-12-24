package org.graviton.game.fight;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.game.fight.common.FightState;
import org.graviton.game.fight.type.DuelFight;
import org.graviton.game.maps.GameMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Botan on 11/12/2016. 02:53
 */

@Data
public class FightFactory {
    private final AtomicInteger identityGenerator = new AtomicInteger(0);

    private final GameMap gameMap;

    private final Map<Integer, Fight> fights = new ConcurrentHashMap<>();

    public FightFactory(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public void newDuel(Player first, Player second) {
        add(new DuelFight(identityGenerator.incrementAndGet(), first, second, gameMap));
    }

    private void add(Fight fight) {
        this.fights.put(fight.getId(), fight);
    }

    public byte getFightSize() {
        return (byte) this.fights.values().stream().filter(fight -> fight.getState() == FightState.ACTIVE).count();
    }

    public Fight get(int fight) {
        return this.fights.get(fight);
    }


}
