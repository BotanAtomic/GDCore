package org.graviton.game.fight.team;

import lombok.Data;
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
public class FightTeam implements Iterable<Fighter> {
    private final List<Fighter> fighters = new ArrayList<>();
    private final Fighter leader;
    private final FightSide side;

    private final List<Cell> cells;

    private boolean locked = false;
    private boolean allowSpectator = false;
    private boolean needHelp = false;

    public FightTeam(Fighter fighter, FightSide side) {
        this.leader = fighter;
        this.side = side;
        this.cells = CellLoader.getFightCells((GameMap) fighter.getCreature().getLocation().getMap(), side);
        add(fighter);
    }

    public void add(Fighter fighter) {
        fighter.setTeam(this);
        fighter.setSide(side);
        this.fighters.add(fighter);
    }

    public void actualizeMap(GameMap gameMap, FightMap fightMap, Fighter fighter) {
        fighter.setLastLocation(fighter.getCreature().getLocation().copy());
        gameMap.out(fighter.getCreature());
        fightMap.register(fighter.getCreature());
        fighter.getCreature().getLocation().setMap(fightMap);
    }

    public void actualizeMap(GameMap gameMap, FightMap fightMap) {
        this.fighters.forEach(fighter -> actualizeMap(gameMap, fightMap, fighter));
    }

    public void send(String data) {
        this.fighters.forEach(fighter -> fighter.send(data));
    }

    public void placeFighter(Fighter fighter) {
        fighter.setFightCell(random());
    }

    public void placeFighters() {
        this.fighters.forEach(this::placeFighter);
    }

    private Cell random() {
        List<Cell> freeCells = this.cells.stream().filter(Cell::isFree).map(cell -> cell).collect(Collectors.toList());
        return freeCells.get(new Random().nextInt(freeCells.size()));
    }

    public boolean containsCell(short cell) {
        return this.cells.stream().anyMatch(cell1 -> cell1.getId() == cell);
    }

    @Override
    public Iterator<Fighter> iterator() {
        return this.fighters.iterator();
    }
}
