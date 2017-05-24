package org.graviton.game.fight;

import javafx.util.Pair;
import lombok.Data;
import org.graviton.constant.Dofus;
import org.graviton.game.client.player.Player;
import org.graviton.game.effect.type.push.PushBackEffect;
import org.graviton.game.effect.type.transport.TranspositionEffect;
import org.graviton.game.fight.common.FightState;
import org.graviton.game.fight.common.FightType;
import org.graviton.game.fight.flag.FlagAttribute;
import org.graviton.game.fight.team.FightTeam;
import org.graviton.game.fight.team.MonsterTeam;
import org.graviton.game.fight.team.PlayerTeam;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final Map<Short, AbstractTrap> traps = new ConcurrentHashMap<>();
    protected FightState state = FightState.INIT;
    protected FightTurnList turnList;
    private ScheduledExecutorService executorService;
    private AtomicInteger idGenerator = new AtomicInteger(-100);

    private int toWait = 0;

    private List<Player> spectator = new ArrayList<>();

    private final Map<Integer, Pair<Player, Byte>> disconnectedPlayers = new ConcurrentHashMap<>();

    protected Fight(ScheduledExecutorService executorService, int id, FightTeam firstTeam, FightTeam secondTeam, GameMap gameMap) {
        this.executorService = executorService;
        this.id = id;
        this.gameMap = gameMap;
        this.fightMap = gameMap.createFightMap(this);

        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;

        initialize();

        generateFlag();

        if (scheduledTime() > 0)
            schedule(this::start, scheduledTime() * 1000);
    }

    public Fighter getFighter(int targetId) {
        Optional<Fighter> fighterOptional = fighters().stream().filter(fighter -> fighter.getId() == targetId).findFirst();
        return fighterOptional.isPresent() ? fighterOptional.get() : null;
    }

    public Fighter getAliveFighter(int targetId) {
        Fighter fighter = getFighter(targetId);
        return fighter != null && !fighter.isDead() ? fighter : null;
    }

    void generateFlag() {
        if (this.state == FightState.PLACE) {
            flagData.clear();
            flagData.add(flagMessage());
            flagData.add(FightPacketFormatter.teamMessage(getFirstTeam().getLeader().getId(), getFirstTeam()));
            flagData.add(FightPacketFormatter.teamMessage(secondTeam instanceof MonsterTeam ? ((MonsterTeam) secondTeam).getMonsterGroup().getId() : getSecondTeam().getLeader().getId(), getSecondTeam()));
            flagData.forEach(gameMap::send);
        }
    }

    private void initialize() {
        this.state = FightState.PLACE;
        List<FightTeam> teams = Arrays.asList(firstTeam, secondTeam);

        send(FightPacketFormatter.newFightMessage(this.state, canQuit(), true, false, scheduledTime(), getType()), teams);

        teams.forEach(fightTeam -> {
            fightTeam.initialize(this);
            fightTeam.actualizeMap(gameMap, fightMap);
            fightTeam.send(FightPacketFormatter.startCellsMessage(gameMap.getPlaces(), fightTeam.getSide()));
            fightTeam.placeFighters();
        });

        send(FightPacketFormatter.showFighters(fighters()));
    }

    public Collection<Fighter> fighters() {
        return Stream.concat(firstTeam.stream(), secondTeam.stream()).filter(new ArrayList<>()::add).collect(Collectors.toList());
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
            fullData.add(FightPacketFormatter.flagAttributeMessage(!team.isAllowSpectator(), FlagAttribute.DENY_SPECTATORS, team.getLeader().getId()));
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
        if (state == FightState.FINISHED)
            return;

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
        return executorService.schedule(runnable, time, TimeUnit.MILLISECONDS);
    }

    private void kickSpectator() {
        spectator.forEach(spectator -> {
            spectator.getAccount().getClient().setEndFight();
            spectator.send(FightPacketFormatter.fighterLeft());
        });
        this.spectator.clear();
    }

    protected void destroy() {
        kickSpectator();

        if (this.turnList != null)
            this.turnList.destroy();

        gameMap.getFightFactory().removeFight(this);
        gameMap.send(GamePacketFormatter.fightCountMessage(gameMap.getFightFactory().getFightSize()));
    }

    protected void kill(Fighter fighter) {
        send(FightPacketFormatter.fighterDieMessage(fighter.getId()));
        this.toWait += 1600;
        fighter.getTeam().setLastDead(fighter);
        fighter.getFightCell().getCreatures().clear();
        fighter.setFightCell(null);
        fighter.setDead(true);
        this.turnList.remove(fighter, fighter.getMaster() != null);

        if (!fighter.getInvocations().isEmpty())
            new ArrayList<>(fighter.getInvocations()).forEach(this::kill);

        if (fighter.getMaster() != null) {
            fighter.getMaster().getInvocations().remove(fighter);
            fighter.getTeam().remove(fighter);
        } else
            check();
    }

    private void check() {
        if (this.firstTeam.stream().filter(current -> current.getMaster() == null && !current.isDead()).count() == 0)
            destroyFight(this.firstTeam.getLeader());
        else if (this.secondTeam.stream().filter(current -> current.getMaster() == null && !current.isDead()).count() == 0)
            destroyFight(this.secondTeam.getLeader());
    }

    public Collection<AbstractTrap> getTrap(short cell) {
        return this.traps.values().stream().filter(trap -> trap instanceof Trap && trap.containCell(cell)).collect(Collectors.toList());
    }

    public void checkGlyph(Fighter fighter) {
        this.traps.values().stream().filter(trap -> trap instanceof Glyph).forEach(abstractTrap -> abstractTrap.check(fighter));
    }

    public int nextId() {
        return idGenerator.incrementAndGet();
    }

    protected long getDuration() {
        return new Interval(startTime.getTime(), new Date().getTime()).toDurationMillis();
    }

    public String information() {
        return FightPacketFormatter.fightMessage(this);
    }

    protected abstract byte scheduledTime();

    protected abstract boolean canQuit();

    public abstract boolean allowDisconnection();

    protected abstract FightType getType();

    protected abstract String flagMessage();

    public void join(FightTeam team, Fighter fighter) {
        if (team.isOnlyGroup() && (((Player) fighter).getGroup() == null || !((Player) fighter).getGroup().equals(((PlayerTeam) team).getGroup()))) {
            fighter.send(FightPacketFormatter.cannotJoinMessage(fighter.getId(), 'f'));
            return;
        }

        if (!team.isLocked()) {
            fighter.send(FightPacketFormatter.newFightMessage(state, canQuit(), true, false, scheduledTime(), getType()));
            team.addFighter(fighter);
            team.actualizeMap(this.gameMap, this.fightMap, fighter);
            fighter.send(FightPacketFormatter.startCellsMessage(this.gameMap.getPlaces(), team.getSide()));
            fighter.setFight(this);
            team.placeFighter(fighter);
            send(FightPacketFormatter.showFighter(fighter), fighter);
            fighter.send(this.fightMap.buildData());
            generateFlag();
        } else
            fighter.send(FightPacketFormatter.cannotJoinMessage(fighter.getId(), 'f'));
    }

    public void joinAsSpectator(Player spectator) {
        if (firstTeam.isAllowSpectator() && secondTeam.isAllowSpectator()) {
            spectator.send(FightPacketFormatter.newFightMessage(state, canQuit(), true, true, scheduledTime(), getType()));
            spectator.send(FightPacketFormatter.showFighters(fighters()));
            spectator.send(FightPacketFormatter.fightStartMessage());
            spectator.send(FightPacketFormatter.turnListMessage(this.turnList.getTurns()));
            spectator.send(FightPacketFormatter.turnStartMessage(this.turnList.getCurrent().getFighter().getId(), Dofus.TURN_TIME));

            gameMap.out(spectator.getCreature());
            fightMap.register(spectator.getCreature());
            this.spectator.add(spectator);
            send(FightPacketFormatter.joinFightAsSpectator(spectator.getName()));
        } else
            spectator.send(FightPacketFormatter.cannotJoinFightAsSpectator());
    }

    public abstract void quit(Fighter fighter);

    private void start() {
        if (state == FightState.ACTIVE)
            return;

        onStart();

        this.state = FightState.ACTIVE;
        this.turnList = new FightTurnList(this);

        Collection<Fighter> fighters = fighters();

        gameMap.send(FightPacketFormatter.removeFlagMessage(this.id));
        this.flagData.clear();

        send(FightPacketFormatter.fightersPlacementMessage(fighters));
        send(FightPacketFormatter.fightStartMessage());
        send(FightPacketFormatter.turnListMessage(this.turnList.getTurns()));

        turnList.getCurrent().getFighter().initializeFighterPoints();
        schedule(() -> this.turnList.getCurrent().begin(), 500);

        this.gameMap.send(GamePacketFormatter.fightCountMessage(this.gameMap.getFightFactory().getFightSize()));
    }

    protected abstract void destroyFight(Fighter looser);

    protected abstract void onStart();

    public void switchSpectator(Fighter fighter) {
        fighter.getTeam().setAllowSpectator(!fighter.getTeam().isAllowSpectator());
        gameMap.send(FightPacketFormatter.flagAttributeMessage(fighter.getTeam().isAllowSpectator(), FlagAttribute.DENY_SPECTATORS, fighter.getTeam().getLeader().getId()));
        fighter.send(MessageFormatter.customMessage(fighter.getTeam().isAllowSpectator() ? "039" : "040"));

        if (!fighter.getTeam().isAllowSpectator())
            kickSpectator();
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

    public void switchGroup(Fighter fighter) {
        fighter.getTeam().setOnlyGroup(!fighter.getTeam().isOnlyGroup());
        gameMap.send(FightPacketFormatter.flagAttributeMessage(fighter.getTeam().isOnlyGroup(), FlagAttribute.ALLOW_PARTY, fighter.getTeam().getLeader().getId()));
        fighter.send(MessageFormatter.customMessage(fighter.getTeam().isOnlyGroup() ? "093" : "094"));
        ((PlayerTeam) fighter.getTeam()).setGroup(fighter.getTeam().isOnlyGroup() ? ((Player) fighter).getGroup() : null);
    }

    public FightTeam otherTeam(FightTeam fightTeam) {
        return fightTeam.getId() == firstTeam.getId() ? secondTeam : firstTeam;
    }

    public void disconnect(Player player) {
        if (this.state == FightState.ACTIVE) {
            this.disconnectedPlayers.put(player.getId(), new Pair<>(player, Dofus.DISCONNECT_REMAINING_TURN));
            player.setConnected(false);
            send(FightPacketFormatter.disconnectedPlayerMessage(player.getName(), Dofus.DISCONNECT_REMAINING_TURN));
        } else
            quit(player);
    }

    public void reconnect(Player player) {
        this.disconnectedPlayers.remove(player.getId());
        player.setConnected(true);
        send(FightPacketFormatter.reconnectedPlayerMessage(player.getName()));
    }
}
