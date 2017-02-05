package org.graviton.game.fight.team;

import lombok.Data;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.fight.FightMap;
import org.graviton.game.maps.utils.CellLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Botan on 11/12/2016. 02:26
 */

@Data
public abstract class FightTeam extends ArrayList<Fighter> implements Iterable<Fighter> {
    protected final FightSide side;
    private Fight fight;
    private Fighter leader;
    private List<Cell> cells;

    private boolean locked = false;
    private boolean allowSpectator = true;
    private boolean needHelp = false;
    private boolean onlyGroup;

    private Fighter lastDead;

    FightTeam(Fighter leader, FightSide side) {
        this.leader = leader;
        this.side = side;
    }

    public abstract void addFighter(Fighter fighter);

    public abstract void actualizeMap(GameMap gameMap, FightMap fightMap, Fighter fighter);

    public void actualizeMap(GameMap gameMap, FightMap fightMap) {
        this.cells = CellLoader.getFightCells(fightMap, gameMap.getPlaces(), side);
        forEach(fighter -> actualizeMap(gameMap, fightMap, fighter));
    }

    public void send(String data) {
        forEach(fighter -> fighter.send(data));
    }

    public void placeFighter(Fighter fighter) {
        fighter.setFightCell(random());
        fighter.setStartCell(fighter.getFightCell());
    }

    public void placeFighters() {
        forEach(this::placeFighter);
    }

    private Cell random() {
        List<Cell> freeCells = this.cells.stream().filter(Cell::isFree).map(cell -> cell).collect(Collectors.toList());
        return freeCells.get(new Random().nextInt(freeCells.size()));
    }

    public boolean containsCell(short cell) {
        return this.cells.stream().anyMatch(cell1 -> cell1.getId() == cell);
    }

    public int realSize() {
        return (int) stream().filter(fighter -> fighter.getMaster() == null).count();
    }

    public abstract void initialize(Fight fight);

    public int getLevel() {
        return stream().mapToInt(Fighter::getLevel).sum();
    }

    @Override
    public Iterator<Fighter> iterator() {
        return super.iterator();
    }
}
