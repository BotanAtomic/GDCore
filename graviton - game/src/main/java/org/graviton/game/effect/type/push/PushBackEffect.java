package org.graviton.game.effect.type.push;

import com.google.common.collect.Lists;
import javafx.util.Pair;
import org.graviton.game.effect.Effect;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.trap.Trap;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.utils.Cells;
import org.graviton.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by Botan on 28/12/2016. 01:46
 */
public class PushBackEffect implements Effect {

    private static Pair<Cell, Collection<Trap>> result(Fighter fighter, short size, Cell initialCell, OrientationEnum orientation, AbstractMap map) {
        Cell lastCell = initialCell;
        Collection<Trap> traps = null;

        for (int a = 0; a < size; a++) {
            Cell nextCell = map.getCells().get(Cells.getCellIdByOrientation(lastCell.getId(), orientation, map.getWidth()));

            if (nextCell != null && nextCell.isWalkable() && nextCell.getCreatures().isEmpty()) {
                lastCell = nextCell;

                Collection<Trap> currentTraps = fighter.getFight().checkTrap(nextCell.getId());

                if (!currentTraps.isEmpty()) {
                    traps = currentTraps;
                    break;
                }

            } else {
                int damage = (int) (Utils.random(1, 8) * ((double) (fighter.getLevel() < 5 ? 5 : fighter.getLevel()) / 50) * (size - a));

                fighter.getFight().hit(fighter, fighter, damage);

                Fighter aroundTarget;

                if ((aroundTarget = nextCell.getCreatures().isEmpty() ? null : fighter.getFight().getFighter(nextCell.getFirstCreature())) != null)
                    fighter.getFight().hit(fighter, aroundTarget, damage / 2);
                break;
            }
        }
        return new Pair<>(lastCell, traps);
    }

    public static void apply(Fighter fighter, Fighter target, Cell selectedCell, short size) {
        OrientationEnum orientation = Cells.getOrientationByCells(fighter.getFightCell(), target.getFightCell(), fighter.getFight().getFightMap().getWidth());

        if (orientation == null)
            return;

        Pair<Cell, Collection<Trap>> result = result(target, size, target.getFightCell(), orientation, fighter.getFight().getFightMap());

        Cell lastCell = result.getKey();
        Collection<Trap> traps = result.getValue();

        target.setFightCell(lastCell);
        fighter.getFight().send(FightPacketFormatter.actionMessage(FightAction.PUSH, fighter.getId(), target.getId(), lastCell.getId()));

        if (traps != null)
            traps.forEach(trap -> trap.onTrapped(target));
    }

    public void applyForTrap(Trap trap, Fight fight, Collection<Cell> cells, SpellEffect effect) {
        Lists.reverse(new ArrayList<>(cells)).forEach(cellTarget -> {
            Fighter fighter = fight.getFighter(cellTarget.getFirstCreature());

            if (fighter != null) {
                OrientationEnum orientation = Cells.getOrientationByCells(trap.getCenter().getId(), fighter.getFightCell().getId(), fighter.getFight().getFightMap());

                if (orientation == null)
                    return;

                Pair<Cell, Collection<Trap>> result = result(fighter, effect.getFirst(), fighter.getFightCell(), orientation, fighter.getFight().getFightMap());

                Cell lastCell = result.getKey();
                Collection<Trap> traps = result.getValue();

                fighter.setFightCell(lastCell);
                fighter.getFight().send(FightPacketFormatter.actionMessage(FightAction.PUSH, fighter.getId(), fighter.getId(), lastCell.getId()));

                if (traps != null)
                    traps.forEach(currentTrap -> currentTrap.onTrapped(fighter));
            }

        });
    }

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> apply(fighter, target, selectedCell, effect.getFirst()));
    }
}
