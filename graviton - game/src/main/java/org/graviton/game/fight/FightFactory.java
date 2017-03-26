package org.graviton.game.fight;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.monster.MonsterGroup;
import org.graviton.game.fight.common.FightState;
import org.graviton.game.fight.type.AggressionFight;
import org.graviton.game.fight.type.DuelFight;
import org.graviton.game.fight.type.MonsterFight;
import org.graviton.game.maps.GameMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Botan on 11/12/2016. 02:53
 */

@Data
public class FightFactory {
    private final AtomicInteger identityGenerator = new AtomicInteger(0);
    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(0);

    private final GameMap gameMap;

    private final Map<Integer, Fight> fights = new ConcurrentHashMap<>();

    public FightFactory(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public void newDuel(Player first, Player second) {
        add(new DuelFight(scheduledExecutorService, identityGenerator.incrementAndGet(), first, second, gameMap));
    }

    public void newMonsterFight(Player first, MonsterGroup second) {
        add(new MonsterFight(scheduledExecutorService, identityGenerator.incrementAndGet(), first, second, gameMap));
    }

    public void newAggressionFight(Player aggressor, Player attacked) {
        add(new AggressionFight(scheduledExecutorService, identityGenerator.incrementAndGet(), aggressor, attacked, gameMap));
    }

    private void add(Fight fight) {
        this.fights.put(fight.getId(), fight);
    }

    void removeFight(Fight fight) {
        this.fights.remove(fight.getId());
    }

    public byte getFightSize() {
        return (byte) this.fights.values().stream().filter(fight -> fight.getState() == FightState.ACTIVE).count();
    }

    public Fight get(int fight) {
        return this.fights.get(fight);
    }


}
