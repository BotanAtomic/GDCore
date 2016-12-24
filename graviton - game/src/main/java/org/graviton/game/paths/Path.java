package org.graviton.game.paths;

import org.graviton.game.fight.Fighter;
import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.utils.Cells;
import org.graviton.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Botan on 20/11/2016 : 11:47
 */
public class Path extends ArrayList<Short> {

    private final short startCell;

    private String newPath = "";

    private short finalCell;
    private OrientationEnum finalOrientation;

    protected Path(String path, AbstractMap map, short cell, Fighter fighter) {
        this.startCell = cell;
        short lastCell = cell;
        int size = map.getWidth() * 2 + 1;

        for (int i = 0; i < path.length(); i += 3) {
            String stepPath = path.substring(i, i + 3);
            short stepCell = Cells.decode(stepPath.substring(1));

            while (lastCell != stepCell && map.getCells().get(lastCell).isWalkable() && size-- > 0)
                add(lastCell = Cells.getCellIdByOrientation(lastCell, stepPath.charAt(0), map.getWidth()));

            lastCell = stepCell;

            newPath += stepPath.charAt(0) + Cells.encode(stepCell);

            if (fighter != null && !getAroundFighters(map, fighter, stepCell).isEmpty())
                return;
        }
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

    protected Collection<Fighter> getAroundFighters(AbstractMap map, Fighter fighter, short cellId) {
        Collection<Fighter> fighters = new ArrayList<>();

        char[] directions = {'h', 'f', 'b', 'd'};

        for (char direction : directions) {
            Cell cell = map.getCells().get(Cells.getCellIdByOrientation(cellId, direction, map.getWidth()));
            if (!cell.getCreatures().isEmpty()) {
                Fighter aroundFighter = fighter.getFight().getFighter(cell.getFirstCreature());
                if (aroundFighter.getTeam().getSide().ordinal() != fighter.getSide().ordinal())
                    fighters.add(aroundFighter);
            }
        }

        return fighters;
    }

}
