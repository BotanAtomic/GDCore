package org.graviton.game.fight.team;

import lombok.Data;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.utils.CellLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Botan on 11/12/2016. 02:26
 */

@Data
public class FightTeam {
    private final List<Fighter> fighters = new ArrayList<>();
    private final Fighter leader;
    private final FightSide side;

    private final List<Cell> cells;

    public FightTeam(Fighter fighter, FightSide side) {
        this.leader = fighter;
        this.side = side;
        this.cells = CellLoader.getFightCells(fighter.getCreature().getLocation().getGameMap(), side);
        add(fighter);
    }

    private void add(Fighter fighter) {
        fighter.setSide(side);
        this.fighters.add(fighter);
    }

    public void outMap(GameMap gameMap) {
        this.fighters.forEach(fighter -> gameMap.out(fighter.getCreature()));
    }

    public void send(String data) {
        this.fighters.forEach(fighter -> fighter.send(data));
    }

    public void placeFighters() {
        this.fighters.forEach(fighter -> fighter.setCell(random()));
    }

    private Cell random() {
        List<Cell> freeCells = this.cells.stream().filter(Cell::isFree).map(cell -> cell).collect(Collectors.toList());
        Cell cell = freeCells.get(new Random().nextInt(freeCells.size()));
        System.err.println(cell.getId());
        return cell;
    }

    public String startCellsMessage() {
        return leader.getCreature().getLocation().getGameMap().getPlaces().split("\\|")[side.ordinal()];
    }
}
