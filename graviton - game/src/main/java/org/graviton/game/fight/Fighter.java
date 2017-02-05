package org.graviton.game.fight;

import lombok.Data;
import org.graviton.api.Creature;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.monster.Monster;
import org.graviton.game.effect.buff.Buff;
import org.graviton.game.effect.buff.type.InvisibleBuff;
import org.graviton.game.effect.state.State;
import org.graviton.game.fight.bonus.FightBonus;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.fight.team.FightTeam;
import org.graviton.game.fight.turn.FightTurn;
import org.graviton.game.intelligence.ArtificialIntelligence;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.position.Location;
import org.graviton.game.spell.Spell;
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
    private Location lastLocation;

    private Fight fight;
    private FightSide side;
    private FightTeam team;
    private FightTurn turn;
    private FightBonus fightBonus;

    private Fighter sacrificed;
    private Fighter holdingBy;
    private Fighter holding;
    private Fighter master;

    private boolean ready = false;
    private boolean dead = false;
    private boolean onAction = false;
    private boolean passTurn = false;
    private boolean visible = true;
    private boolean dodgeAttack = false;
    private boolean isStatic = false;

    private byte currentActionPoint, currentMovementPoint, returnSpell;
    private int lastLife;
    private short damageSuffer = 0;

    private List<Short> launchedSpells = new ArrayList<>();
    private List<Buff> buffs = new ArrayList<>();
    private List<State> states = new ArrayList<>();
    private Map<Short, Short> spellBoost = new HashMap<>();
    private List<Fighter> invocations = new ArrayList<>();

    private Cell startCell;

    public static Comparator<Fighter> compareByInitiative() {
        return Comparator.comparingInt(Fighter::getInitiative);
    }

    public static Comparator<Fighter> compareByProspection() {
        return Comparator.comparingInt(Fighter::getProspection);
    }

    public abstract int getId();

    public abstract String getName();

    public abstract short getLevel();

    public abstract Creature getCreature();

    public abstract Statistics getStatistics();

    public abstract void send(String data);

    public abstract String getFightGM();

    public abstract ArtificialIntelligence artificialIntelligence();

    public boolean isStatic() {
        return this.states.contains(State.Rooted) || isStatic;
    }

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
        getFight().send(FightPacketFormatter.actionMessage(FightAction.INVISIBLE_EVENT, getId(), getId(), turns));
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

    public int getInitiative() {
        return getStatistics().get(CharacteristicType.Initiative).total();
    }

    public int getProspection() {
        return getStatistics().get(CharacteristicType.Prospection).total();
    }

    public Cell getFightCell() {
        return this.getCreature().getLocation().getCell();
    }

    public void setFightCell(Cell cell) {
        if (getFightCell() != null)
            getFightCell().getCreatures().remove(getId());
        if (cell != null) {
            cell.getCreatures().add(getId());
            this.getCreature().getLocation().setCell(cell);
        }
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
            buff.clear();
            this.buffs.remove(buff);
        });
    }

    private void destroy() {
        if (fightBonus != null) {
            fightBonus.validate();
            this.fightBonus = null;
        }

        this.lastLocation = null;
        this.fight = null;
        this.side = null;
        this.team = null;
        this.ready = false;
        this.turn = null;
        this.dead = false;
        this.visible = true;
        this.master = null;
        this.buffs.clear();
        this.spellBoost.clear();
        this.launchedSpells.clear();
        this.getStatistics().clearBuffs();
    }

    public void left(boolean activeFight) {
        this.team.remove(this);
        fight.getFightMap().out(getCreature());
        getCreature().setLocation(this.lastLocation);
        ((Player) getCreature()).getAccount().getClient().setEndFight();
        send(FightPacketFormatter.fighterLeft());

        if (!activeFight)
            fight.generateFlag();

        destroy();
    }

    public boolean canReturnSpell(Spell spell) {
        if (this.returnSpell >= spell.getLevel()) {
            fight.send(FightPacketFormatter.actionMessage(FightAction.RETURN_SPELL, getId(), getId(), 1));
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getCreature().getId();
    }

    @Override
    public String toString() {
        return this.getName();
    }


    public boolean isInvocation() {
        return master != null && this instanceof Monster;
    }

}
