package org.graviton.utils;


import com.google.common.collect.ImmutableMap;
import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.maths.Point;
import org.graviton.maths.Vector;

import java.util.Map;
import java.util.Optional;

/**
 * Created by Botan on 19/11/2016 : 11:01
 */
public final class Cells {

    public static final Map<OrientationEnum, Vector> VECTORS =
            ImmutableMap.<OrientationEnum, Vector>builder()
                    .put(OrientationEnum.EAST, Vector.create(1, -1))
                    .put(OrientationEnum.SOUTH_EAST, Vector.create(1, 0))
                    .put(OrientationEnum.SOUTH, Vector.create(1, 1))
                    .put(OrientationEnum.SOUTH_WEST, Vector.create(0, 1))
                    .put(OrientationEnum.WEST, Vector.create(-1, 1))
                    .put(OrientationEnum.NORTH_WEST, Vector.create(-1, 0))
                    .put(OrientationEnum.NORTH, Vector.create(-1, -1))
                    .put(OrientationEnum.NORTH_EAST, Vector.create(0, -1))
                    .build();

    public static Point position(short cellId, int mapWidth) {
        int floor = (int) Math.floor(cellId / (mapWidth * 2 - 1));
        int modulo = (cellId - floor * (mapWidth * 2 - 1)) % mapWidth;

        int x = (cellId - (mapWidth - 1) * (floor - modulo)) / mapWidth;
        int y = floor - modulo;

        return new Point(x, y);
    }

    public static String encode(short cell) {
        return Character.toString(Utils.HASH[(cell / 64)]) + Character.toString(Utils.HASH[((cell % 64))]);
    }

    public static short decode(String string) {
        return (short) (Utils.EXTENDED_ALPHABET.indexOf(string.charAt(0)) * 64 + Utils.EXTENDED_ALPHABET.indexOf(string.charAt(1)));
    }

    public static short getCellIdByOrientation(short cellId, char orientation, byte mapWidth) {
        return getCellIdByOrientation(cellId, OrientationEnum.valueOf((byte) Utils.EXTENDED_ALPHABET.indexOf(orientation)), mapWidth);
    }

    public static OrientationEnum getOrientationByPoints(Point a, Point b) {
        Vector vector = Vector.fromPoints(a, b);
        Optional<OrientationEnum> optional = VECTORS.keySet().stream().filter(orientationEnum -> VECTORS.get(orientationEnum).hasSameDirectionOf(vector)).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

    public static OrientationEnum getOrientationByCells(Cell firstCell, Cell secondCell, int mapWidth) {
        return getOrientationByPoints(position(firstCell.getId(), mapWidth), position(secondCell.getId(), mapWidth));
    }

    public static short getCellIdByOrientation(short cellId, OrientationEnum orientation, byte mapWidth) {
        switch (orientation) {
            case EAST:
                return (short) (cellId + 1);
            case SOUTH_EAST:
                return (short) (cellId + mapWidth);
            case SOUTH:
                return (short) (cellId + (mapWidth * 2 - 1));
            case SOUTH_WEST:
                return (short) (cellId + (mapWidth - 1));
            case WEST:
                return (short) (cellId - 1);
            case NORTH_WEST:
                return (short) (cellId - mapWidth);
            case NORTH:
                return (short) (cellId - (mapWidth * 2 - 1));
            case NORTH_EAST:
                return (short) (cellId - mapWidth + 1);

            default:
                throw new IllegalArgumentException("invalid orientation");
        }
    }

    public static OrientationEnum getOrientationByCells(short firstCell, short secondCell, AbstractMap map) {
        for (OrientationEnum orientation : OrientationEnum.ADJACENTS) {
            short cell = firstCell;
            for (int i = 0; i <= 64; i++) {
                if (getCellIdByOrientation(cell, orientation, map.getWidth()) == secondCell)
                    return orientation;
                cell = getCellIdByOrientation(cell, orientation, map.getWidth());
            }
        }
        return null;
    }

}
