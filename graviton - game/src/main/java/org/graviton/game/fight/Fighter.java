package org.graviton.game.fight;

import org.graviton.api.Creature;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.statistics.common.Statistics;

/**
 * Created by Botan on 10/12/2016. 21:53
 */

public abstract class Fighter {
    private Cell cell;
    private FightSide side;

    public abstract int getId();

    public abstract String getName();

    public abstract Creature getCreature();

    public abstract Statistics getStatistics();

    public abstract void send(String data);

    public abstract String getFightGM();

    public void setCell(Cell cell) {
        cell.getCreatures().add(getId());
        this.cell = cell;
    }

    public Cell getFightCell() {
        return this.cell;
    }

    public FightSide getSide() {
        return this.side;
    }

    public void setSide(FightSide side) {
        this.side = side;
    }
}
