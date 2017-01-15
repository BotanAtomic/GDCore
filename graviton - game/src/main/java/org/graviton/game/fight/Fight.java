package org.graviton.game.fight;

import lombok.Data;
import org.graviton.game.effect.type.push.PushBackEffect;
import org.graviton.game.effect.type.transport.TranspositionEffect;
import org.graviton.game.fight.common.FightState;
import org.graviton.game.fight.common.FightType;
import org.graviton.game.fight.flag.FlagAttribute;
import org.graviton.game.fight.team.FightTeam;
import org.graviton.game.fight.turn.FightTurnList;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.fight.FightMap;
import org.graviton.game.paths.Path;
import org.graviton.game.trap.AbstractTrap;
import org.graviton.game.trap.Glyph;
import org.graviton.game.trap.Trap;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;
import org.joda.time.Interval;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Botan on 10/12/2016. 21:44
 */

@Data
public abstract class Fight {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final Date startTime = new Date();
    private final int id;

    private final GameMap gameMap;
    private final FightMap fightMap;
    private final FightTeam firstTeam, secondTeam;

    private final List<String> flagData = new LinkedList<>();
    private final Map<Short, AbstractTrap> traps = new ConcurrentHashMap<>();
    protected FightState state = FightState.INIT;
    protected FightTurnList turnList;

    protected Fight(int id, FightTeam firstTeam, FightTeam secondTeam, GameMap gameMap) {
        this.id = id;
        this.gameMap = gameMap;
        this.fightMap = gameMap.createFightMap(this);

        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;

        initialize();

        fighters().forEach(fighter -> {
            fighter.setFight(this);
            fighter.initializeFighterPoints();
        });

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

    private void initialize() {
        this.state = FightState.PLACE;
        List<FightTeam> teams = Arrays.asList(firstTeam, secondTeam);

        send(FightPacketFormatter.newFightMessage(this.state, canQuit(), true, false, scheduledTime(), getType()), teams);

        teams.forEach(fightTeam -> {
            fightTeam.actualizeMap(gameMap, fightMap);
            fightTeam.send(FightPacketFormatter.startCellsMessage(gameMap.getPlaces(), fightTeam.getSide()));
            fightTeam.placeFighters();
        });

        send(FightPacketFormatter.showFighters(fighters()));
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
        fightMap.send(data);
    }

    protected void send(String data, Fighter exclude) {
        fighters().stream().filter(fighter -> fighter.getId() != exclude.getId()).forEach(fighter -> fighter.send(data));
    }

    public void changeFighterPlace(Fighter fighter, short cellId) {
        Cell cell = fightMap.getCells().get(cellId);

        if (cell.getCreatures().isEmpty() && fighter.getTeam().containsCell(cellId) && !fighter.isReady()) {
            fighter.setFightCell(cell);
            send(FightPacketFormatter.fighterPlacementMessage(fighter.getId(), cellId));
        }
    }

    public List<String> buildFlag() {
        if (flagData.isEmpty())
            return new ArrayList<>();

        List<String> fullData = new LinkedList<>(flagData);

        Arrays.asList(firstTeam, secondTeam).forEach(team -> {
            fullData.add(FightPacketFormatter.flagAttributeMessage(team.isAllowSpectator(), FlagAttribute.DENY_SPECTATORS, team.getLeader().getId()));
            fullData.add(FightPacketFormatter.flagAttributeMessage(team.isLocked(), FlagAttribute.DENY_ALL, team.getLeader().getId()));
            fullData.add(FightPacketFormatter.flagAttributeMessage(team.isNeedHelp(), FlagAttribute.NEED_HELP, team.getLeader().getId()));
        });

        return fullData;
    }

    public void setReady(Fighter fighter, boolean ready) {
        fighter.setReady(ready);


        if (fighters().stream().filter(current -> !current.isReady()).count() == 0)
            start();
        else
            send(FightPacketFormatter.fighterReadyMessage(fighter.getId(), ready));
    }

    public void hit(Fighter fighter, Fighter target, int damage) {
        if (target.isDodgeAttack() && Path.getAroundFighters(fightMap, fighter, fighter.getFightCell().getId()).contains(target))
            PushBackEffect.apply(fighter, target, null, (short) 1);
        else {
            if (target.getSacrificedFighter() != null) {
                TranspositionEffect.transpose(target, target.getSacrificed());
                target = target.getSacrificed();
            }

            target.getLife().remove(damage);
            send(FightPacketFormatter.lifeEventMessage(fighter.getId(), target.getId(), (damage * -1)));

            if (target.getLife().getCurrent() <= 0)
                kill(target);
        }
    }

    public ScheduledFuture<?> schedule(Runnable runnable, long time) {
        return scheduler.schedule(runnable, time, TimeUnit.MILLISECONDS);
    }

    public void switchSpectator(Fighter fighter) {
        fighter.getTeam().setAllowSpectator(!fighter.getTeam().isAllowSpectator());
        gameMap.send(FightPacketFormatter.flagAttributeMessage(fighter.getTeam().isAllowSpectator(), FlagAttribute.DENY_SPECTATORS, fighter.getTeam().getLeader().getId()));
        fighter.send(MessageFormatter.customMessage(fighter.getTeam().isAllowSpectator() ? "039" : "040"));
    }

    public void switchLocked(Fighter fighter) {
        fighter.getTeam().setLocked(!fighter.getTeam().isLocked());
        gameMap.send(FightPacketFormatter.flagAttributeMessage(fighter.getTeam().isLocked(), FlagAttribute.DENY_ALL, fighter.getTeam().getLeader().getId()));
        fighter.send(MessageFormatter.customMessage(fighter.getTeam().isLocked() ? "095" : "096"));
    }

    public void switchHelp(Fighter fighter) {
        fighter.getTeam().setNeedHelp(!fighter.getTeam().isNeedHelp());
        gameMap.send(FightPacketFormatter.flagAttributeMessage(fighter.getTeam().isNeedHelp(), FlagAttribute.NEED_HELP, fighter.getTeam().getLeader().getId()));
        fighter.send(MessageFormatter.customMessage(fighter.getTeam().isNeedHelp() ? "0103" : "0104"));
    }

    protected void destroy() {
        gameMap.getFightFactory().getFights().remove(getId());
        this.scheduler.shutdownNow();
        gameMap.send(GamePacketFormatter.fightCountMessage(gameMap.getFightFactory().getFightSize()));
    }

    protected void kill(Fighter fighter) {
        fighter.setDead(true);
        this.turnList.remove(fighter);
        send(FightPacketFormatter.fighterDieMessage(fighter.getId()));

        schedule(this::check, 1800); //time for spell animation
    }

    private void check() {
        if (getFirstTeam().getFighters().stream().filter(current -> !current.isDead()).count() == 0)
            destroyFight(getFirstTeam().getLeader());
        else if (getSecondTeam().getFighters().stream().filter(current -> !current.isDead()).count() == 0)
            destroyFight(getSecondTeam().getLeader());
    }

    public Collection<AbstractTrap> getTrap(short cell) {
        return this.traps.values().stream().filter(trap -> trap instanceof Trap && trap.containCell(cell)).collect(Collectors.toList());
    }

    public void checkGlyph(Fighter fighter) {
        this.traps.values().stream().filter(trap -> trap instanceof Glyph).forEach(abstractTrap -> abstractTrap.check(fighter));
    }


    protected long getDuration() {
        return new Interval(startTime.getTime(), new Date().getTime()).toDurationMillis();
    }

    public abstract String information();

    protected abstract byte scheduledTime();

    protected abstract boolean canQuit();

    protected abstract FightType getType();

    public abstract void join(FightTeam team, Fighter fighter);

    public abstract void quit(Fighter fighter);

    public abstract void start();

    protected abstract void destroyFight(Fighter looser);

}
