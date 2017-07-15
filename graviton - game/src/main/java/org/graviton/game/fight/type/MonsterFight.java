package org.graviton.game.fight.type;

import org.graviton.game.action.fight.AbstractFightAction;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.monster.MonsterGroup;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.bonus.DropBonus;
import org.graviton.game.fight.bonus.MonsterFightBonus;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.fight.common.FightState;
import org.graviton.game.fight.common.FightType;
import org.graviton.game.fight.team.FightTeam;
import org.graviton.game.fight.team.MonsterTeam;
import org.graviton.game.fight.team.PlayerTeam;
import org.graviton.game.maps.GameMap;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.graviton.collection.CollectionQuery.from;

/**
 * Created by Botan on 22/01/2017. 15:03
 */
public class MonsterFight extends Fight {


    public MonsterFight(ScheduledExecutorService executorService, int id, Fighter player, MonsterGroup monsterGroup, GameMap gameMap, boolean allowFlag) {
        super(executorService, id, new PlayerTeam(player, FightSide.BLUE), new MonsterTeam(monsterGroup, FightSide.RED), gameMap, allowFlag);

        if (monsterGroup.getRespawnTime() != 0)
            schedule(() -> gameMap.register(monsterGroup.copy(), true), monsterGroup.getRespawnTime());
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
        return FightType.MONSTER;
    }

    @Override
    public void quit(Fighter fighter) {
        if (state == FightState.PLACE) {
            if (fighter.getTeam().getLeader().getId() == fighter.getId())
                fighter.getTeam().setOtherLeader(fighter);
            fighter.left(false);
        } else if (state != FightState.FINISHED)
            if (fighter.getTeam().realSize() == 1)
                destroyFight(fighter);
            else
                abandon(fighter);
    }

    @Override
    protected void destroyFight(Fighter looser) {
        if (state == FightState.FINISHED)
            return;

        state = FightState.FINISHED;
        if (looser.getTeam() instanceof PlayerTeam)
            onFighterLoose();
        else
            onFighterWin();

        if (((MonsterTeam) getSecondTeam()).getMonsterGroup().getRespawnTime() == 0)
            schedule(() -> getGameMap().register(getGameMap().randomMonsterGroup(), true), TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES));
    }

    @Override
    protected void onStart() {

    }

    private void onFighterLoose() {
        send(endMessage(false));
        schedule(() -> {
            List<Player> players = from(getFirstTeam()).filter(fighter -> !fighter.isInvocation()).transform(fighter -> (Player) fighter).computeList(new ArrayList<>());
            players.forEach(player -> {
                player.getStatistics().addEnergy((short) (player.getLevel() * -5));
                player.getStatistics().getLife().set(0);
                player.left(true);
            });
            schedule(() -> players.forEach(Player::returnToLastLocation), 300);
            super.destroy();
        }, 2500);

    }

    private void onFighterWin() {
        final DropBonus dropBonus = new DropBonus((PlayerTeam) getFirstTeam(), (MonsterTeam) getSecondTeam());
        getFirstTeam().stream().filter(fighter -> !fighter.isInvocation()).forEach(fighter -> fighter.setFightBonus(new MonsterFightBonus(this, (Player) fighter, dropBonus)));
        send(endMessage(true));
        schedule(() -> {
            List<Fighter> winners = new ArrayList<>(getFirstTeam()).stream().filter(fighter -> !fighter.isInvocation()).collect(Collectors.toList());
            winners.forEach(fighter -> fighter.left(true));

            AbstractFightAction abstractFightAction = getFirstTeam().getLeader().getCreature().entityFactory().getFightAction(getGameMap().getId());
            if (abstractFightAction != null)
                schedule(() -> winners.stream().map(fighter -> (Player) fighter).forEach(abstractFightAction::apply), 300);

            super.destroy();
        }, 1800 + getToWait());
    }

    private String endMessage(boolean playerWin) {
        FightTeam winner = playerWin ? getFirstTeam() : getSecondTeam();
        return FightPacketFormatter.fightEndMessage(getDuration(), winner.getLeader().getId(), winner, !playerWin ? getFirstTeam() : getSecondTeam());
    }

    @Override
    protected String flagMessage() {
        return FightPacketFormatter.addMonsterFlagMessage(super.getId(), getType(), getFirstTeam().getLeader(), ((MonsterTeam) getSecondTeam()).getMonsterGroup());
    }

    private void abandon(Fighter fighter) {
        if (fighter.getTeam().getLeader() == fighter)
            fighter.getTeam().setOtherLeader(fighter);

        fighter.getStatistics().getLife().set(0);
        kill(fighter);
        fighter.left(true);
    }

}
