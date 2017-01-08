package org.graviton.game.fight;

import lombok.Data;
import org.graviton.api.Creature;
import org.graviton.game.client.player.Player;
import org.graviton.game.effect.buff.Buff;
import org.graviton.game.effect.buff.type.InvisibleBuff;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.fight.team.FightTeam;
import org.graviton.game.fight.turn.FightTurn;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.position.Location;
import org.graviton.game.statistics.Life;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.common.Statistics;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Botan on 10/12/2016. 21:53
 */

@Data
public abstract class Fighter {
    private static final short INVISIBLE_ACTION = 150;
    private Fight fight;
    private FightSide side;
    private FightTeam team;
    private FightTurn turn;
    private boolean ready = false;
    private boolean dead = false;
    private boolean onAction = false;
    private boolean passTurn = false;
    private boolean visible = true;
    private boolean dodgeAttack = false;
    private byte currentActionPoint;
    private byte currentMovementPoint;
    private Location lastLocation;
    private int lastLife;
    private short damageSuffer = 0;
    private List<Short> launchedSpells = new ArrayList<>();
    private List<Buff> buffs = new ArrayList<>();
    private Map<Short, Short> spellBoost = new HashMap<>();
    private Fighter sacrificed;

    public static Comparator<Fighter> compareByInitiative() {
        return Comparator.comparingInt(Fighter::getInitiative);
    }

    public abstract int getId();

    public abstract String getName();

    public abstract short getLevel();

    public abstract Creature getCreature();

    public abstract Statistics getStatistics();

    public abstract void send(String data);

    public abstract String getFightGM();

    public void addLaunchedSpell(short spell) {
        this.launchedSpells.add(spell);
    }

    public void clearLaunchedSpell() {
        this.launchedSpells.clear();
    }

    public boolean hasLaunchedSpell(short spell) {
        return this.launchedSpells.contains(spell);
    }

    public void addBuff(Buff buff) {
        this.buffs.add(buff);
    }

    public void removeSpellBoost(short spellTemplate) {
        this.spellBoost.remove(spellTemplate);
    }

    public void addSpellBoost(short spellTemplate, short boost) {
        this.spellBoost.put(spellTemplate, (short) (boost + this.spellBoost.getOrDefault(spellTemplate, (short) 0)));
    }

    public short getSpellBoost(short spellTemplate) {
        return this.spellBoost.getOrDefault(spellTemplate, (short) 0);
    }

    public void removeBuff(Buff buff) {
        this.buffs.remove(buff);
    }

    public void setVisible(boolean visible, short turns) {
        this.visible = visible;
        getFight().send(FightPacketFormatter.actionMessage(INVISIBLE_ACTION, getId(), getId(), turns));
    }

    public void setVisibleAfterAttack() {
        Buff buff = getBuff(InvisibleBuff.class);
        buff.destroy();
        this.buffs.remove(buff);
    }

    public void passTurn() {
        this.passTurn = true;
        if (this.fight.getTurnList().getCurrent() == this.turn) {
            this.turn.end();
            this.passTurn = false;
        }
    }

    public Buff getBuff(Class<?> buffClass) {
        Optional<Buff> buffOptional = this.buffs.stream().filter(buff -> buff.getClass().equals(buffClass)).findFirst();
        return buffOptional.isPresent() ? buffOptional.get() : null;
    }

    public Collection<Buff> getBuffs(Class<?> buffClass) {
        return this.buffs.stream().filter(buff -> buff.getClass().equals(buffClass)).collect(Collectors.toList());
    }

    public void addActionPoint(byte value) {
        this.currentActionPoint += value;
        if (currentActionPoint < 0)
            currentActionPoint = 0;
    }

    public void addMovementPoint(byte value) {
        this.currentMovementPoint += value;
        if (currentMovementPoint < 0)
            currentMovementPoint = 0;
    }

    public void refreshStatistics() {
        if (getCreature() instanceof Player)
            send(PlayerPacketFormatter.asMessage((Player) getCreature()));
    }

    public byte getCurrentPoint(CharacteristicType characteristicType) {
        switch (characteristicType) {
            case ActionPoints:
                return getCurrentActionPoint();
            case MovementPoints:
                return getCurrentMovementPoint();
            default:
                return 0;
        }
    }

    public void initializeFighterPoints() {
        this.currentActionPoint = (byte) getCreature().getStatistics().get(CharacteristicType.ActionPoints).total();
        this.currentMovementPoint = (byte) getCreature().getStatistics().get(CharacteristicType.MovementPoints).total();
    }

    public short getInitiative() {
        return getStatistics().get(CharacteristicType.Initiative).total();
    }

    public Cell getFightCell() {
        return this.getCreature().getLocation().getCell();
    }

    public void setFightCell(Cell cell) {
        getFightCell().getCreatures().remove(getId());
        cell.getCreatures().add(getId());
        this.getCreature().getLocation().setCell(cell);
    }

    public FightSide getSide() {
        return this.side;
    }

    public void setSide(FightSide side) {
        this.side = side;
    }

    public Location getLocation() {
        return getCreature().getLocation();
    }

    public Life getLife() {
        return getCreature().getStatistics().getLife();
    }

    Fighter getSacrificedFighter() {
        return sacrificed != null ? sacrificed.isDead() ? null : sacrificed : null;
    }

    public int getRate(int base, boolean critical) {
        if (base == 0)
            return 0;

        int value = base - getStatistics().get(critical ? CharacteristicType.CriticalHit : CharacteristicType.CriticalFailure).total();
        return value < 2 ? 2 : value;
    }

    public void clearBuffs() {
        new ArrayList<>(this.buffs).forEach(buff -> {
            buff.destroy();
            this.buffs.remove(buff);
        });
    }

    private void destroy() {
        this.lastLocation = null;
        this.fight = null;
        this.side = null;
        this.team = null;
        this.ready = false;
        this.turn = null;
        this.dead = false;
        this.visible = true;
        this.buffs.clear();
        this.spellBoost.clear();
        this.launchedSpells.clear();
        this.getStatistics().clearBuffs();
    }

    public void quit() {
        this.team.getFighters().remove(this);
        fight.getFightMap().out(getCreature());
        getCreature().setLocation(this.lastLocation);
        ((Player) getCreature()).getAccount().getClient().setEndFight();
        send(FightPacketFormatter.fighterLeft());
        destroy();
    }

    public void left() {
        this.team.getFighters().remove(this);
        fight.getFightMap().out(getCreature());
        getCreature().setLocation(this.lastLocation);
        ((Player) getCreature()).getAccount().getClient().setEndFight();
        send(FightPacketFormatter.fighterLeft());
        destroy();
        fight.generateFlag();
    }

    @Override
    public int hashCode() {
        return this.getCreature().getId();
    }

    @Override
    public String toString() {
        return this.getName();
    }


}
