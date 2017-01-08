package org.graviton.game.spell.zone.type;

import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.spell.zone.Zone;
import org.graviton.utils.Cells;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Botan on 25/12/2016. 20:02
 */
public class LineZone extends Zone {

    public LineZone(SpellEffect spellEffect, String zone) {
        super(spellEffect, zone);
    }

    @Override
    public Collection<Cell> getCells(Cell initialCell, Fighter fighter) {
        AbstractMap map = fighter.getLocation().getMap();

        List<Cell> cells = new CopyOnWriteArrayList<>();
        cells.add(initialCell);

        calculateLine(super.getLength(), initialCell, cells, map, Cells.getOrientationByCells(fighter.getFightCell(), initialCell, map.getWidth()));

        return cells;
    }
}
