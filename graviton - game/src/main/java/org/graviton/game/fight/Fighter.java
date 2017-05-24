package org.graviton.game.fight;

import lombok.Data;
import org.graviton.game.creature.Creature;
import org.graviton.game.alignment.Alignment;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.monster.Monster;
import org.graviton.game.creature.monster.extra.Double;
import org.graviton.game.effect.buff.Buff;
import org.graviton.game.effect.buff.BuffManager;
import org.graviton.game.effect.buff.type.InvisibleBuff;
import org.graviton.game.effect.state.State;
import org.graviton.game.fight.bonus.FightBonus;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.fight.team.FightTeam;
import org.graviton.game.fight.turn.FightTurn;
import org.graviton.game.intelligence.api.ArtificialIntelligence;
import org.graviton.game.spell.SpellCounter;
import org.graviton.game.spell.SpellFilter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.position.Location;
import org.graviton.game.spell.Spell;
import org.graviton.game.statistics.Life;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.common.Statistics;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

import java.util.*;

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

    private final SpellCounter spellCounter = new SpellCounter();
    private final SpellFilter spellFilter = new SpellFilter(this);

    private final BuffManager buffManager = new BuffManager();

    private boolean ready = false;
    private boolean dead = false;
    private boolean onAction = false;
    private boolean passTurn = false;
    private boolean visible = true;
    private boolean dodgeAttack = false;
    private boolean isStatic = false;
    private boolean connected = true;

    private byte currentActionPoint, currentMovementPoint, returnSpell;
    private int lastLife;
    private short damageSuffer = 0;

    private List<Fighter> invocations = new ArrayList<>();

    private Cell startCell;
    private final List<Runnable> toExecute = new ArrayList<>();


    public static Comparator<Fighter> compareByInitiative() {
        return Comparator.comparingInt(Fighter::getInitiative);
    }

    public static Comparator<Fighter> compareByProspection() {
        return Comparator.comparingInt(Fighter::getProspection);
    }

    public static Comparator<Fighter> compareByLife() {
        return Comparator.comparingInt(fighter -> fighter.getLife().getCurrent());
    }

    public abstract int getId();

    public abstract String getName();

    public abstract short getLevel();

    public abstract Creature getCreature();

    public abstract Statistics getStatistics();

    public abstract void send(String data);

    public abstract String getFightGM();

    public abstract String doubleGm(Double clone);

    public abstract ArtificialIntelligence artificialIntelligence();

    public abstract List<Spell> getSpells();

    public abstract Alignment getAlignment();

    public boolean isStatic() {
        return buffManager.hasState(State.Rooted) || isStatic;
    }

    public void setVisible(boolean visible, short turns) {
        this.visible = visible;
        fight.send(FightPacketFormatter.actionMessage(FightAction.INVISIBLE_EVENT, getId(), getId(), turns));
    }

    public void setVisibleAfterAttack() {
        Buff buff = buffManager.getBuff(InvisibleBuff.class);
        buff.destroy();
        buffManager.removeBuff(buff);
    }

    public void passTurn() {
        this.passTurn = true;
        if (this.fight.getTurnList().getCurrent().getFighter().getId() == this.turn.getFighter().getId()) {
            this.turn.end(false);
            this.passTurn = false;
        }
    }

    public boolean isAlive() {
        return !dead;
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
                return this.currentActionPoint;
            case MovementPoints:
                return this.currentMovementPoint;
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

    private void destroy() {
        if (fightBonus != null)
            this.fightBonus = null;

        this.lastLocation = null;
        this.fight = null;
        this.side = null;
        this.team = null;
        this.ready = false;
        this.turn = null;
        this.dead = false;
        this.visible = true;
        this.master = null;
        this.buffManager.clear();
        spellCounter.reset();
        this.getStatistics().clearBuffs();
        getLife().blockRegeneration(false);
    }

    public void left(boolean activeFight) {
        this.team.remove(this);
        fight.getFightMap().out(getCreature());
        getCreature().setLocation(this.lastLocation);
        ((Player) getCreature()).getAccount().getClient().setEndFight();
        send(FightPacketFormatter.fighterLeft());
        getLife().refreshRegenTime();

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
        return master != null && (this instanceof Monster || this instanceof Double);
    }

    public boolean canLaunchSpell(short spell, byte maxPerTurn, int target, byte maxPerTarget) {
        return spellCounter.canLaunchSpell(spell, maxPerTurn, target, maxPerTarget);
    }

    public boolean canLaunchSpell(Spell spell, int target) {
        return spellCounter.canLaunchSpell(spell.getTemplate().getId(), spell.getMaxPerTurn(), target, spell.getMaxPerPlayer());
    }
}
