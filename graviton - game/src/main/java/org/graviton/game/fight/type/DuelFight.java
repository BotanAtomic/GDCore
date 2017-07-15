package org.graviton.game.fight.type;

import org.graviton.game.client.player.Player;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.fight.common.FightState;
import org.graviton.game.fight.common.FightType;
import org.graviton.game.fight.team.FightTeam;
import org.graviton.game.fight.team.PlayerTeam;
import org.graviton.game.maps.GameMap;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Botan on 10/12/2016. 21:47
 */
public class DuelFight extends Fight {

    public DuelFight(ScheduledExecutorService executorService, int id, Player first, Player second, GameMap gameMap) {
        super(executorService, id, new PlayerTeam(first, FightSide.RED), new PlayerTeam(second, FightSide.BLUE), gameMap, true);
    }

    @Override
    protected byte scheduledTime() {
        return 0;
    }

    @Override
    protected boolean canQuit() {
        return true;
    }

    @Override
    public boolean allowDisconnection() {
        return false;
    }

    @Override
    protected FightType getType() {
        return FightType.DUEL;
    }

    @Override
    protected String flagMessage() {
        return FightPacketFormatter.addFlagMessage(super.getId(), getType(), getFirstTeam().getLeader(), getSecondTeam().getLeader());
    }

    @Override
    public void quit(Fighter fighter) {
        if (super.state != FightState.FINISHED) {
            fighter.getTeam().setOtherLeader(fighter);

            if (fighter.getTeam().realSize() == 1) {
                if (state == FightState.ACTIVE)
                    destroyFight(fighter);
                else
                    cancelFight();
            } else
                onFighterLeft(fighter);
        }
    }

    private void onFighterLeft(Fighter fighter) {
        if (state == FightState.ACTIVE) {
            onFighterQuit(fighter);
            return;
        }
        if (fighter.getTeam().getLeader().getId() == fighter.getId())
            cancelFight();
        else {
            if (fighter.getTeam().getLeader() == fighter)
                fighter.getTeam().setOtherLeader(fighter);
            fighter.left(false);
        }
    }

    private void onFighterQuit(Fighter fighter) {
        kill(fighter);
        fighter.left(true);
    }

    @Override
    protected void destroyFight(Fighter looser) {
        if(state == FightState.FINISHED)
            return;

        state = FightState.FINISHED;
        send(endMessage(looser));
        schedule(() -> {
            super.fighters().stream().filter(fighter -> !fighter.isInvocation()).forEach(fighter -> {
                fighter.getLife().set(fighter.getLastLife());
                fighter.left(true);
            });
            super.destroy();
        }, 1000 + getToWait());

    }

    @Override
    protected void onStart() {

    }

    private void cancelFight() {
        getGameMap().send(FightPacketFormatter.removeFlagMessage(getId()));
        super.fighters().forEach(fighter -> fighter.left(true));
        super.destroy();
    }

    private String endMessage(Fighter fighter) {
        FightTeam winner = otherTeam(fighter.getTeam());
        return FightPacketFormatter.fightEndMessage(getDuration(), winner.getLeader().getId(), winner, fighter.getTeam());
    }

}
