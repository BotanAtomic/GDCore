package org.graviton.game.spell.zone.type;

import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.spell.zone.Zone;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by Botan on 25/12/2016. 19:58
 */
public class SingleCellZone extends Zone {

    public SingleCellZone(SpellEffect spellEffect, String zone) {
        super(spellEffect, zone);
    }

    @Override
    public Collection<Cell> getCells(Cell initialCell, Fighter fighter) {
        return Collections.singletonList(initialCell);
    }
}
