package org.graviton.game.effect.type.push;

import org.graviton.game.effect.Effect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.look.enums.Orientation;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.trap.AbstractTrap;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.utils.Cells;

import java.util.Collection;

/**
 * Created by Botan on 02/01/2017. 17:24
 */
public class PushFearEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        AbstractMap map = fighter.getFight().getFightMap();
        Orientation orientation = Cells.getOrientationByCells(fighter.getFightCell().getId(), selectedCell.getId(), map);

        short fighterCell = Cells.getCellIdByOrientation(fighter.getFightCell().getId(), orientation, map.getWidth());

        Cell lastCell = map.getCells().get(fighterCell);
        Fighter target = fighter.getFight().getFighter(lastCell.getFirstCreature());


        if (target != null) {
            Collection<AbstractTrap> traps = null;

            while (true) {
                Cell currentCell = map.getCells().get(Cells.getCellIdByOrientation(lastCell.getId(), orientation, map.getWidth()));

                if (currentCell == null || !currentCell.getCreatures().isEmpty() || !currentCell.isWalkable())
                    break;

                lastCell = currentCell;

                Collection<AbstractTrap> currentTraps = fighter.getFight().getTrap(currentCell.getId());

                if (!currentTraps.isEmpty()) {
                    traps = currentTraps;
                }

                if (currentCell.getId() == selectedCell.getId())
                    break;
            }

            target.setFightCell(lastCell);
            fighter.getFight().send(FightPacketFormatter.actionMessage(FightAction.PUSH, fighter.getId(), target.getId(), lastCell.getId()));

            if (traps != null)
                traps.forEach(currentTrap -> currentTrap.onTrapped(target));
        }
    }

    @Override
    public Effect copy() {
        return new PushFearEffect();
    }
}
