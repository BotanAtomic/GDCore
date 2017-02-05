package org.graviton.game.paths;

import org.graviton.game.effect.state.State;
import org.graviton.game.fight.Fighter;
import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.trap.AbstractTrap;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.utils.Cells;
import org.graviton.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Botan on 20/11/2016 : 11:47
 */
public class Path extends ArrayList<Short> {

    private final short startCell;
    protected List<Runnable> tasks = new ArrayList<>();
    private String newPath = "";
    private short finalCell;
    private OrientationEnum finalOrientation;

    protected Path(String path, AbstractMap map, short cell) {
        this.startCell = cell;
        short lastCell = cell;
        int size = map.getWidth() * 2 + 1;

        for (int i = 0; i < path.length(); i += 3) {
            String stepPath = path.substring(i, i + 3);
            short stepCell = Cells.decode(stepPath.substring(1));

            while (lastCell != stepCell && map.getCells().containsKey(lastCell) && map.getCells().get(lastCell).isWalkable() && size-- > 0)
                add(lastCell = Cells.getCellIdByOrientation(lastCell, stepPath.charAt(0), map.getWidth()));

            lastCell = stepCell;
            newPath += stepPath.charAt(0) + Cells.encode(lastCell);
        }
    }

    protected Path(String path, AbstractMap map, short cell, Fighter fighter) {
        this.startCell = cell;
        short lastCell = cell;
        int size = map.getWidth() * 2 + 1;

        for (int i = 0; i < path.length(); i += 3) {
            String stepPath = path.substring(i, i + 3);
            short stepCell = Cells.decode(stepPath.substring(1));


            while (lastCell != stepCell && map.getCells().get(lastCell).isWalkable() && size-- > 0) {
                lastCell = Cells.getCellIdByOrientation(lastCell, stepPath.charAt(0), map.getWidth());

                Cell last = map.getCells().get(lastCell);
                if (!last.getCreatures().isEmpty() && fighter.getFight().getFighter(last.getFirstCreature()) != null) {
                    fighter.send(MessageFormatter.customMessage("0161"));
                    fighter.send(FightPacketFormatter.showCellMessage(fighter.getId(), last.getId()));
                    return;
                }

                add(lastCell);
                newPath += stepPath.charAt(0) + Cells.encode(lastCell);

                Collection<AbstractTrap> traps = fighter.getFight().getTrap(lastCell);

                if (!traps.isEmpty()) {
                    fighter.getFight().getTrap(lastCell).forEach(trap -> tasks.add((() -> trap.onTrapped(fighter))));
                    return;
                }

                if (fighter.getHoldingBy() != null) {
                    fighter.getHoldingBy().getStates().remove(State.Carrier);
                    fighter.getHoldingBy().setHolding(null);

                    fighter.setHoldingBy(null);
                    fighter.getStates().remove(State.Carried);
                    return;
                }

                if (!getAroundFighters(map, fighter, lastCell).isEmpty())
                    return;
            }

            lastCell = stepCell;
        }
    }

    public static Collection<Fighter> getAroundFighters(AbstractMap map, Fighter fighter, short cellId) {
        Collection<Fighter> fighters = new ArrayList<>();

        for (OrientationEnum orientation : OrientationEnum.ADJACENTS) {
            Cell cell = map.getCells().get(Cells.getCellIdByOrientation(cellId, orientation, map.getWidth()));
            if (!cell.getCreatures().isEmpty()) {
                Fighter aroundFighter = fighter.getFight().getFighter(cell.getFirstCreature());
                if (aroundFighter != null && aroundFighter.getTeam().getSide().ordinal() != fighter.getSide().ordinal() && aroundFighter.isVisible())
                    fighters.add(aroundFighter);
            }
        }

        return fighters;
    }

    protected boolean isValid() {
        return size() != 0;
    }

    protected void initialize() {
        this.finalCell = Cells.decode(newPath.substring(newPath.length() - 2));
        this.finalOrientation = OrientationEnum.valueOf(Utils.parseBase64Char(newPath.charAt(newPath.length() - 3)));
    }

    protected short getCell() {
        return finalCell;
    }

    protected OrientationEnum getOrientation() {
        return finalOrientation;
    }

    @Override
    public String toString() {
        return this.newPath = ('a' + Cells.encode(startCell).concat(newPath));
    }

}
