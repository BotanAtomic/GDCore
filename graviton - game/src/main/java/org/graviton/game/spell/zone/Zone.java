package org.graviton.game.spell.zone;

import lombok.Data;
import org.graviton.collection.NoDuplicatesList;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.spell.common.Target;
import org.graviton.utils.Cells;
import org.graviton.utils.Utils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Botan on 25/12/2016. 19:38
 */

@Data
public abstract class Zone {
    private final SpellEffect spellEffect;
    private final byte length;

    public Zone(SpellEffect spellEffect, String zone) {
        this.spellEffect = spellEffect;
        this.length = (byte) Utils.EXTENDED_ALPHABET.indexOf(zone.charAt(1));
    }

    protected static void calculateLine(short length, Cell firstCell, List<Cell> cells, AbstractMap map, OrientationEnum orientation) {
        AtomicReference<Cell> lastCell = new AtomicReference<>(firstCell);

        IntStream.range(0, length).forEach(i -> {
            Cell cell = map.getCells().get(Cells.getCellIdByOrientation(lastCell.get().getId(), orientation, map.getWidth()));

            if (cell == null)
                return;

            cells.add(cell);
            lastCell.set(cell);
        });
    }

    public abstract Collection<Cell> getCells(Cell initialCell, Fighter fighter);

    public Collection<Fighter> getTargets(Collection<Cell> cells, Fighter fighter) {
        Fight fight = fighter.getFight();
        return cells.stream().filter(cell -> fight.getFighter(cell.getFirstCreature()) != null).filter(cell -> {
            Fighter target = fight.getFighter(cell.getFirstCreature());
            switch (spellEffect.getTarget()) {
                case ALL:
                    return true;
                case EXCLUDE_PLAYER:
                    return target.getId() != fighter.getId();
                case PLAYER:
                    return target.getId() == fighter.getId();
                case PLAYER_AND_ALLY:
                case ALLY:
                    return target.getSide() == fighter.getSide();
                case ENEMY:
                    return target.getSide() != fighter.getSide();
                case INVOCATION:
                    return target.isInvocation();
                case ALLY_EXCLUDE_PLAYER:
                    return target.getSide() == fighter.getSide() && target.getId() != fighter.getId();
            }
            return false;
        }).map(cell -> spellEffect.getTarget() == Target.PLAYER ? fighter : fight.getFighter(cell.getFirstCreature())).collect(Collectors.toCollection(NoDuplicatesList::new));
    }

    public Collection<Fighter> getTargets(Cell initialCell, Fighter fighter) {
        return getTargets(getCells(initialCell, fighter), fighter);
    }

}
