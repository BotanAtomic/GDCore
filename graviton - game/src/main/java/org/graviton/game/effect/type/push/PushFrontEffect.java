package org.graviton.game.effect.type.push;

import org.graviton.game.effect.Effect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.trap.Trap;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.utils.Cells;

import java.util.Collection;

import static java.lang.String.valueOf;

/**
 * Created by Botan on 28/12/2016. 16:29
 */
public class PushFrontEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            AbstractMap map = fighter.getFight().getFightMap();
            OrientationEnum orientation = Cells.getOrientationByCells(target.getFightCell(), fighter.getFightCell(), map.getWidth());

            Collection<Trap> traps = null;
            Cell lastCell = target.getFightCell();

            for (int a = 0; a < effect.getFirst(); a++) {
                Cell nextCell = map.getCells().get(Cells.getCellIdByOrientation(lastCell.getId(), orientation, map.getWidth()));

                if (nextCell == null || !nextCell.isWalkable() || !nextCell.getCreatures().isEmpty())
                    break;

                lastCell = nextCell;

                Collection<Trap> currentTraps = fighter.getFight().checkTrap(nextCell.getId());

                if (!currentTraps.isEmpty()) {
                    traps = currentTraps;
                    break;
                }
            }

            target.setFightCell(lastCell);
            fighter.getFight().send(FightPacketFormatter.actionMessage(FightAction.PUSH, fighter.getId(), valueOf(target.getId()), valueOf(lastCell.getId())));

            if (traps != null)
                traps.forEach(trap -> trap.onTrapped(target));
        });
    }
}
