package org.graviton.game.fight.team;

import lombok.Getter;
import org.graviton.collection.CollectionQuery;
import org.graviton.game.creature.monster.Monster;
import org.graviton.game.creature.monster.MonsterGroup;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.fight.FightMap;
import org.graviton.game.position.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Botan on 22/01/2017. 15:39
 */
public class MonsterTeam extends FightTeam {
    @Getter
    private final MonsterGroup monsterGroup;

    public MonsterTeam(MonsterGroup monsterGroup, FightSide side) {
        super(null, side);
        monsterGroup.getLocation().getMap().out(monsterGroup);
        this.monsterGroup = monsterGroup;
    }

    @Override
    public void addFighter(Fighter fighter) {
        if (super.getLeader() == null)
            super.setLeader(fighter);

        ((Monster) fighter).setId(getFight().nextId());
        fighter.setFight(getFight());
        fighter.initializeFighterPoints();
        fighter.setTeam(this);
        fighter.setSide(side);
        fighter.setReady(true);
        add(fighter);
    }

    @Override
    public void actualizeMap(GameMap gameMap, FightMap fightMap, Fighter fighter) {
        fighter.getCreature().setLocation(Location.empty());
        fightMap.register(fighter.getCreature());
        fighter.getCreature().getLocation().setMap(fightMap);
    }

    @Override
    public void initialize(Fight fight) {
        super.setFight(fight);
        monsterGroup.getMonsters().forEach(monster -> addFighter(monster.copy()));
    }

    public List<Monster> getMonsters() {
        return CollectionQuery.from(this).filter(fighter -> !fighter.isInvocation()).transform(fighter -> (Monster) fighter).computeList(new ArrayList<>());
    }
}
