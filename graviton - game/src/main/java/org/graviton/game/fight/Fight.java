package org.graviton.game.fight;

import lombok.Data;
import org.graviton.game.fight.common.FightState;
import org.graviton.game.fight.common.FightType;
import org.graviton.game.fight.flag.FlagAttribute;
import org.graviton.game.fight.team.FightTeam;
import org.graviton.game.fight.turn.FightTurnList;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.fight.FightMap;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Botan on 10/12/2016. 21:44
 */

@Data
public abstract class Fight {
    private final Date startTime = new Date();
    private final int id;
    private final GameMap gameMap;
    private final FightMap fightMap;
    private final FightTeam firstTeam, secondTeam;
    private final List<String> flagData = new LinkedList<>();
    protected FightState state = FightState.INIT;
    protected FightTurnList turnList;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    protected Fight(int id, FightTeam firstTeam, FightTeam secondTeam, GameMap gameMap) {
        this.id = id;
        this.gameMap = gameMap;
        this.fightMap = gameMap.createFightMap(this);

        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;

        startInitialization();

        fighters().forEach(fighter -> fighter.setFight(this));

        generateFlag();
    }

    public Fighter getFighter(int targetId) {
        final Fighter[] target = {null};
        Arrays.asList(firstTeam, secondTeam).forEach(team -> team.getFighters().stream().filter(fighter -> fighter.getId() == targetId).findFirst().ifPresent(fighter -> target[0] = fighter));
        return target[0];
    }

    protected void generateFlag() {
        if (this.state == FightState.PLACE) {
            flagData.clear();
            flagData.add(FightPacketFormatter.addFlagMessage(id, getType(), getFirstTeam().getLeader(), getSecondTeam().getLeader()));
            flagData.add(FightPacketFormatter.teamMessage(getFirstTeam().getLeader().getId(), getFirstTeam().getFighters()));
            flagData.add(FightPacketFormatter.teamMessage(getSecondTeam().getLeader().getId(), getSecondTeam().getFighters()));
            flagData.forEach(gameMap::send);
        }
    }

    private void startInitialization() {
        this.state = FightState.PLACE;
        List<FightTeam> teams = Arrays.asList(firstTeam, secondTeam);

        send(FightPacketFormatter.newFightMessage(this.state, canQuit(), true, false, scheduledTime(), getType()), teams);

        teams.forEach(fightTeam -> {
            fightTeam.actualizeMap(gameMap, fightMap);
            fightTeam.send(FightPacketFormatter.startCellsMessage(gameMap.getPlaces(), fightTeam.getSide()));
            fightTeam.placeFighters();
        });

        send(FightPacketFormatter.showFighters(fighters()), teams);
    }

    public Collection<Fighter> fighters() {
        return Stream.concat(firstTeam.getFighters().stream(), secondTeam.getFighters().stream()).filter(new ArrayList<>()::add).collect(Collectors.toList());
    }

    protected void send(String data, List<Fighter> fighters) {
        fighters.forEach(fighter -> fighter.send(data));
    }

    private void send(String data, Collection<FightTeam> teams) {
        teams.forEach(fightTeam -> fightTeam.send(data));
    }


    public void send(String data) {
        fighters().forEach(fighter -> fighter.send(data));
    }

    protected void send(String data, Fighter exclude) {
        fighters().stream().filter(fighter -> fighter.getId() != exclude.getId()).forEach(fighter -> fighter.send(data));
    }

    public void changeFighterPlace(Fighter fighter, short cellId) {
        Cell cell = fightMap.getCells().get(cellId);

        if (cell.getCreatures().isEmpty() && fighter.getTeam().containsCell(cellId)) {
            fighter.setFightCell(cell);
            send(FightPacketFormatter.fighterPlacementMessage(fighter.getId(), cellId));
        }
    }

    public List<String> buildFlag() {
        List<String> fullData = new LinkedList<>();
        fullData.addAll(flagData);

        if (fullData.isEmpty())
            return fullData;

        Arrays.asList(firstTeam, secondTeam).forEach(team -> {
            fullData.add(FightPacketFormatter.flagAttributeMessage(team.isAllowSpectator(), FlagAttribute.DENY_SPECTATORS, team.getLeader().getId()));
            fullData.add(FightPacketFormatter.flagAttributeMessage(team.isLocked(), FlagAttribute.DENY_ALL, team.getLeader().getId()));
            fullData.add(FightPacketFormatter.flagAttributeMessage(team.isNeedHelp(), FlagAttribute.NEED_HELP, team.getLeader().getId()));
        });

        return fullData;
    }

    public void setReady(Fighter fighter, boolean ready) {
        fighter.setReady(ready);

        final AtomicBoolean startFight = new AtomicBoolean(true);
        fighters().forEach(current -> {
            current.send(FightPacketFormatter.fighterReadyMessage(fighter.getId(), ready));
            if (!current.isReady())
                startFight.set(false);
        });

        if (startFight.get())
            start();
    }

    public ScheduledFuture<?> schedule(Runnable runnable, long time) {
        return scheduler.schedule(runnable, time, TimeUnit.MILLISECONDS);
    }

    public void switchSpectator(Fighter fighter) {
        FightTeam team = fighter.getTeam();
        team.setAllowSpectator(!team.isAllowSpectator());
        String packet = FightPacketFormatter.flagAttributeMessage(team.isAllowSpectator(), FlagAttribute.DENY_SPECTATORS, fighter.getTeam().getLeader().getId());
        gameMap.send(packet);
    }

    public void switchLocked(Fighter fighter) {
        FightTeam team = fighter.getTeam();
        team.setLocked(!team.isLocked());
        String packet = FightPacketFormatter.flagAttributeMessage(team.isLocked(), FlagAttribute.DENY_ALL, fighter.getTeam().getLeader().getId());
        gameMap.send(packet);
    }

    public void switchHelp(Fighter fighter) {
        FightTeam team = fighter.getTeam();
        team.setNeedHelp(!team.isNeedHelp());
        String packet = FightPacketFormatter.flagAttributeMessage(team.isNeedHelp(), FlagAttribute.NEED_HELP, fighter.getTeam().getLeader().getId());
        gameMap.send(packet);
    }

    protected void destroy() {
        gameMap.getFightFactory().getFights().remove(getId());
        this.scheduler.shutdownNow();
    }

    protected void kill(Fighter fighter) {
        this.turnList.remove(fighter);
        send(FightPacketFormatter.fighterDieMessage(fighter.getId()), fighter);
    }

    public abstract String information();

    protected abstract byte scheduledTime();

    protected abstract boolean canQuit();

    protected abstract FightType getType();

    public abstract void join(FightTeam team, Fighter fighter);

    public abstract void quit(Fighter fighter);

    public abstract void start();

}
