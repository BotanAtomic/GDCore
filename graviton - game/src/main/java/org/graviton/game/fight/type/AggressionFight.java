package org.graviton.game.fight.type;

import org.graviton.constant.Dofus;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.monster.Monster;
import org.graviton.game.creature.monster.MonsterTemplate;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.bonus.AggressionFightBonus;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.fight.common.FightState;
import org.graviton.game.fight.common.FightType;
import org.graviton.game.fight.team.FightTeam;
import org.graviton.game.fight.team.PlayerTeam;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.position.Location;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

import static org.graviton.collection.CollectionQuery.from;
import static org.graviton.constant.Dofus.Monster.KNIGHT;
import static org.graviton.game.alignment.type.AlignmentType.NEUTRE;

/**
 * Created by Botan on 10/12/2016. 21:47
 */
public class AggressionFight extends Fight {

    public AggressionFight(ScheduledExecutorService executorService, int id, Player first, Player second, GameMap gameMap) {
        super(executorService, id, new PlayerTeam(first, FightSide.RED), new PlayerTeam(second, FightSide.BLUE), gameMap, true);
    }

    @Override
    protected byte scheduledTime() {
        return 45;
    }

    @Override
    protected boolean canQuit() {
        return false;
    }

    @Override
    public boolean allowDisconnection() {
        return true;
    }

    @Override
    protected FightType getType() {
        return FightType.AGGRESSION;
    }

    @Override
    protected String flagMessage() {
        return FightPacketFormatter.addAggressionFlagMessage(super.getId(), getType(), getFirstTeam().getLeader(), getSecondTeam().getLeader());
    }

    @Override
    public void quit(Fighter fighter) {
        if (state == FightState.PLACE) {
            if (fighter.getTeam().getLeader().getId() == fighter.getId())
                fighter.getTeam().setOtherLeader(fighter);
            abandon(fighter);
        } else if (state != FightState.FINISHED)
            if (fighter.getTeam().realSize() == 1)
                destroyFight(fighter);
            else
                abandon(fighter);
    }


    @Override
    protected void destroyFight(Fighter looser) {
        if(state == FightState.FINISHED)
            return;

        state = FightState.FINISHED;

        new AggressionFightBonus(looser.getTeam(), otherTeam(looser.getTeam()));

        send(endMessage(looser));

        byte looserTeamId = looser.getTeam().getId();

        schedule(() -> {
            List<Player> players = from(fighters()).filter(fighter -> fighter instanceof Player).transform(fighter -> (Player) fighter).computeList(new ArrayList<>());

            players.forEach(player -> {
                if (player.getTeam().getId() == looserTeamId) {
                    player.getStatistics().addEnergy((short) (player.getLevel() * -5));
                    player.getStatistics().getLife().set(0);
                    schedule(player::returnToLastLocation, 500);
                }
                player.left(true);
            });

            super.destroy();
        }, 2000 + getToWait());

    }

    @Override
    protected void onStart() {
        if (getSecondTeam().getLeader().getAlignment().getType() == NEUTRE) {
            Cell cell = getSecondTeam().random();

            if (cell != null) {
                Monster monster = getFirstTeam().getLeader().getCreature().entityFactory().getMonsterTemplate(KNIGHT).getByLevel(Dofus.knightLevel(getFirstTeam().getLeader().getLevel()));
                getSecondTeam().addFighter(monster);

                monster.getCreature().setLocation(Location.empty());
                getFightMap().register(monster.getCreature());
                monster.getCreature().getLocation().setMap(getFightMap());

                monster.setFight(this);
                getSecondTeam().placeFighter(monster, cell);
                send(FightPacketFormatter.showFighter(monster));
            }
        }
    }

    private String endMessage(Fighter fighter) {
        FightTeam winner = otherTeam(fighter.getTeam());

        if (winner.realPlayer() == 0 || fighter.getTeam().realPlayer() == 0)
            return "";

        return FightPacketFormatter.aggressionFightEndMessage(getDuration(), winner.getLeader().getId(), winner, fighter.getTeam());
    }

    private void abandon(Fighter fighter) {
        if (Objects.equals(fighter.getTeam().getLeader(), fighter))
            fighter.getTeam().setOtherLeader(fighter);

        Player player = (Player) fighter;
        player.getStatistics().addEnergy((short) (player.getLevel() * -5));
        schedule(player::returnToLastLocation, 500);
        player.getStatistics().getLife().set(0);
        kill(fighter);
        fighter.left(true);
    }

}
