package org.graviton.game.spell.zone.type;

import org.graviton.game.fight.Fighter;
import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.spell.zone.Zone;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Botan on 25/12/2016. 20:02
 */
public class CrossZone extends Zone {

    public CrossZone(SpellEffect spellEffect, String zone) {
        super(spellEffect, zone);
    }

    @Override
    public Collection<Cell> getCells(Cell initialCell, Fighter fighter) {
        AbstractMap map = fighter.getLocation().getMap();

        List<Cell> cells = new CopyOnWriteArrayList<>();

        for (OrientationEnum orientation : OrientationEnum.ADJACENT)
            calculateLine(super.getLength(), initialCell, cells, map, orientation);

        cells.add(initialCell);
        return cells;
    }

}
