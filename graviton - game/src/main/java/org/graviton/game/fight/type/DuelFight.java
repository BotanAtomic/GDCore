package org.graviton.game.fight.type;

import org.graviton.game.client.player.Player;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.fight.common.FightState;
import org.graviton.game.fight.common.FightType;
import org.graviton.game.fight.team.FightTeam;
import org.graviton.game.fight.turn.FightTurnList;
import org.graviton.game.maps.GameMap;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Botan on 10/12/2016. 21:47
 */
public class DuelFight extends Fight {

    public DuelFight(int id, Player first, Player second, GameMap gameMap) {
        super(id, new FightTeam(first, FightSide.RED), new FightTeam(second, FightSide.BLUE), gameMap);
    }

    @Override
    public String information() {
        return FightPacketFormatter.duelFightMessage(this);
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
    protected FightType getType() {
        return FightType.DUEL;
    }

    @Override
    public void join(FightTeam team, Fighter fighter) {
        if (!team.isLocked()) {
            fighter.send(FightPacketFormatter.newFightMessage(super.state, canQuit(), true, false, scheduledTime(), getType()));
            team.add(fighter);
            team.actualizeMap(getGameMap(), getFightMap(), fighter);
            fighter.send(FightPacketFormatter.startCellsMessage(getGameMap().getPlaces(), team.getSide()));
            fighter.setFight(this);
            team.placeFighter(fighter);
            send(FightPacketFormatter.showFighter(fighter), fighter);
            fighter.send(getFightMap().buildData());
            generateFlag();
        } else
            fighter.send(FightPacketFormatter.cannotJoinMessage(fighter.getId(), 'f'));
    }

    @Override
    public void quit(Fighter fighter) {
        if (fighter.getTeam().getFighters().size() == 1) {
            if (DuelFight.super.state == FightState.ACTIVE)
                destroyFight(fighter);
            else
                cancelFight();
        } else {
            onFighterLeft(fighter);
        }
    }

    @Override
    public void start() {
        super.state = FightState.ACTIVE;
        super.turnList = new FightTurnList(this);
        List<Fighter> fighters = new ArrayList<>(fighters());

        getGameMap().send(FightPacketFormatter.removeFlagMessage(getId()));
        super.getFlagData().clear();

        send(FightPacketFormatter.fightersPlacementMessage(fighters), fighters);
        send(FightPacketFormatter.fightStartMessage(), fighters);
        send(FightPacketFormatter.turnListMessage(super.turnList.getTurns()), fighters);
        send(FightPacketFormatter.fighterInformationMessage(fighters), fighters);

        super.turnList.getCurrent().startTurn();

        getGameMap().send(GamePacketFormatter.fightCountMessage(getGameMap().getFightFactory().getFightSize()));
    }

    private void onFighterLeft(Fighter fighter) {
        if (DuelFight.super.state == FightState.ACTIVE) {
            onFighterQuit(fighter);
            return;
        }

        if (fighter.getTeam().getLeader().getId() == fighter.getId())
            cancelFight();
        else
            fighter.left();

    }

    private void onFighterQuit(Fighter fighter) {
        kill(fighter);
        fighter.quit();
    }

    private void destroyFight(Fighter fighter) {
        send(endMessage(fighter));
        schedule(() -> {
            super.fighters().forEach(Fighter::quit);
            super.destroy();
        }, 1000);
    }

    private void cancelFight() {
        getGameMap().send(FightPacketFormatter.removeFlagMessage(getId()));
        getGameMap().getFightFactory().getFights().remove(getId());

        super.fighters().forEach(Fighter::quit);
        super.destroy();
    }

    private String endMessage(Fighter fighter) {
        FightTeam winner = fighter.getTeam().getSide().ordinal() == getFirstTeam().getSide().ordinal() ? getSecondTeam() : getFirstTeam();
        FightTeam looser = winner.getSide().ordinal() == getFirstTeam().getSide().ordinal() ? getSecondTeam() : getFirstTeam();

        return FightPacketFormatter.fightEndMessage(new Interval(super.getStartTime().getTime(), new Date().getTime()).toDurationMillis(), winner.getLeader(), winner.getFighters(), looser.getFighters());
    }

}
