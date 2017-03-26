package org.graviton.game.fight.team;

import lombok.Getter;
import lombok.Setter;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.group.Group;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.fight.FightMap;
import org.graviton.network.game.protocol.GamePacketFormatter;

/**
 * Created by Botan on 22/01/2017. 15:40
 */
public class PlayerTeam extends FightTeam {
    @Getter
    @Setter
    private Group group;

    public PlayerTeam(Fighter fighter, FightSide side) {
        super(fighter, side);
        addFighter(fighter);
    }

    @Override
    public void addFighter(Fighter fighter) {
        fighter.setLastLife(fighter.getLife().getCurrent());
        fighter.send(GamePacketFormatter.fightRegenTimerMessage((short) 0));
        fighter.setTeam(this);
        fighter.setSide(side);
        fighter.getLife().applyRegenTime();
        fighter.getLife().blockRegeneration(true);
        add(fighter);
    }

    @Override
    public void actualizeMap(GameMap gameMap, FightMap fightMap, Fighter fighter) {
        fighter.setLastLocation(fighter.getCreature().getLocation().copy());
        gameMap.out(fighter.getCreature());
        fightMap.register(fighter.getCreature());
        fighter.getCreature().getLocation().setMap(fightMap);
    }

    @Override
    public void initialize(Fight fight) {
        super.setFight(fight);
        forEach(fighter -> {
            fighter.setFight(fight);
            fighter.initializeFighterPoints();
        });
    }
}
