package org.graviton.game.spell.zone.type;

import org.graviton.game.fight.Fighter;
import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.spell.zone.Zone;
import org.graviton.utils.Cells;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

/**
 * Created by Botan on 25/12/2016. 19:41
 */
public class CircleZone extends Zone {

    public CircleZone(SpellEffect spellEffect, String zone) {
        super(spellEffect, zone);
    }


    @Override
    public Collection<Cell> getCells(Cell initialCell, Fighter fighter) {
        AbstractMap map = fighter.getLocation().getMap();

        List<Cell> cells = new CopyOnWriteArrayList<>();
        cells.add(initialCell);

        IntStream.range(0, getLength()).forEach(i -> cells.forEach(current -> {
            for (OrientationEnum orientation : OrientationEnum.ADJACENTS) {
                Cell cell = map.getCells().get(Cells.getCellIdByOrientation(current.getId(), orientation, map.getWidth()));

                if (cell == null)
                    continue;

                if (!cells.contains(cell))
                    cells.add(cell);

            }
        }));

        return cells;
    }


}
