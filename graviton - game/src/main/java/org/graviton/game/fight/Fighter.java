package org.graviton.game.fight;

import lombok.Data;
import org.graviton.api.Creature;
import org.graviton.game.client.player.Player;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.fight.team.FightTeam;
import org.graviton.game.fight.turn.FightTurn;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.position.Location;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.common.Statistics;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.Comparator;

/**
 * Created by Botan on 10/12/2016. 21:53
 */

@Data
public abstract class Fighter {

    private Fight fight;
    private FightSide side;
    private FightTeam team;
    private FightTurn turn;
    private boolean ready = false;
    private boolean dead = false;
    private byte currentActionPoint;
    private byte currentMovementPoint;
    private Location lastLocation;

    public static Comparator<Fighter> compareByInitiative() {
        return Comparator.comparingInt(o -> o.getInitiative());
    }

    public abstract int getId();

    public abstract String getName();

    public abstract short getLevel();

    public abstract Creature getCreature();

    public abstract Statistics getStatistics();

    public abstract void send(String data);

    public abstract String getFightGM();

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

    private void destroy() {
        this.lastLocation = null;
        this.fight = null;
        this.side = null;
        this.team = null;
        this.ready = false;
        this.turn = null;
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
