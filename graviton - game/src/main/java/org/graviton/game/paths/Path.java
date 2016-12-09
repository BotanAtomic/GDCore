package org.graviton.game.paths;

import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.game.maps.GameMap;
import org.graviton.utils.Cells;
import org.graviton.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Botan on 20/11/2016 : 11:47
 */
public class Path extends ArrayList<Short> {

    private final short startCell;
    private final String initPath;

    private String newPath;

    private short finalCell;
    private OrientationEnum finalOrientation;

    protected Path(String path, GameMap map, short cell) {
        this.initPath = path;
        this.startCell = cell;
        short lastCell = cell;
        int size = map.getWidth() * 2 + 1;

        for (int i = 0; i < path.length(); i += 3) {
            String stepPath = path.substring(i, i + 3);
            short stepCell = Cells.decode(stepPath.substring(1));

            while (lastCell != stepCell && map.getCells().get(lastCell).isWalkable() && size-- > 0)
                add(lastCell = Cells.getCellIdByOrientation(lastCell, stepPath.charAt(0), map.getWidth()));


            lastCell = stepCell;
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
        return this.newPath = ('a' + Cells.encode(startCell).concat(initPath));
    }

}
