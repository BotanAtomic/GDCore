package org.graviton.utils;


import com.google.common.collect.ImmutableMap;
import org.graviton.game.fight.Fighter;
import org.graviton.game.look.enums.Orientation;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.paths.PathMaker;
import org.graviton.maths.Point;
import org.graviton.maths.Vector;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by Botan on 19/11/2016 : 11:01
 */
public final class Cells {

    private static final Map<Orientation, Vector> VECTORS =
            ImmutableMap.<Orientation, Vector>builder()
                    .put(Orientation.EAST, Vector.create(1, -1))
                    .put(Orientation.SOUTH_EAST, Vector.create(1, 0))
                    .put(Orientation.SOUTH, Vector.create(1, 1))
                    .put(Orientation.SOUTH_WEST, Vector.create(0, 1))
                    .put(Orientation.WEST, Vector.create(-1, 1))
                    .put(Orientation.NORTH_WEST, Vector.create(-1, 0))
                    .put(Orientation.NORTH, Vector.create(-1, -1))
                    .put(Orientation.NORTH_EAST, Vector.create(0, -1))
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
        return (short) ((Utils.EXTENDED_ALPHABET.indexOf(string.charAt(0)) << 6) + Utils.EXTENDED_ALPHABET.indexOf(string.charAt(1)));
    }

    public static short getCellIdByOrientation(short cellId, char orientation, byte mapWidth) {
        return getCellIdByOrientation(cellId, Orientation.valueOf((byte) Utils.EXTENDED_ALPHABET.indexOf(orientation)), mapWidth);
    }

    private static Orientation getOrientationByPoints(Point a, Point b) {
        Vector vector = Vector.fromPoints(a, b);
        Optional<Orientation> optional = VECTORS.keySet().stream().filter(orientationEnum -> VECTORS.get(orientationEnum).hasSameDirectionOf(vector)).findFirst();
        return optional.orElse(null);
    }

    public static Orientation getOrientationByCells(Cell firstCell, Cell secondCell, int mapWidth) {
        return getOrientationByPoints(position(firstCell.getId(), mapWidth), position(secondCell.getId(), mapWidth));
    }

    public static short getCellIdByOrientation(short cellId, Orientation orientation, byte mapWidth) {
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

    public static Orientation getOrientationByCells(short firstCell, short secondCell, AbstractMap map) {
        for (Orientation orientation : Orientation.ADJACENT) {
            short cell = firstCell;
            for (int i = 0; i <= 64; i++) {
                if (getCellIdByOrientation(cell, orientation, map.getWidth()) == secondCell)
                    return orientation;
                cell = getCellIdByOrientation(cell, orientation, map.getWidth());
            }
        }
        return null;
    }

    public static short distanceBetween(byte width, short firstCell, short secondCell) {
        return (short) (Math.abs(getXCoordinates(width, firstCell) - getXCoordinates(width, secondCell)) + Math.abs(getYCoordinates(width, firstCell) - getYCoordinates(width, secondCell)));
    }

    private static int getXCoordinates(byte width, short cell) {
        return ((cell - (width - 1) * getYCoordinates(width, cell)) / width);
    }

    private static int getYCoordinates(byte width, short cell) {
        int loc5 = (cell / ((width << 1) - 1));
        int loc6 = cell - loc5 * ((width << 1) - 1);
        int loc7 = loc6 % width;
        return (loc5 - loc7);
    }

    public static boolean isNextTo(short firstCell, short secondCell) {
        return firstCell + 14 == secondCell || firstCell + 15 == secondCell || firstCell - 14 == secondCell || firstCell - 15 == secondCell;
    }

    public static short getNearestCellAround(AbstractMap map, short startCell, short endCell) {
        int requiredDistance = 1000;
        short resultCell = startCell;

        for (Orientation orientation : Orientation.ADJACENT) {
            Cell newCell = map.getCells().get(getCellIdByOrientation(startCell, orientation, map.getWidth()));

            if (newCell == null)
                continue;

            int distance = distanceBetween(map.getWidth(), endCell, newCell.getId());

            if (distance < requiredDistance && newCell.isWalkable() && newCell.getCreatures().isEmpty()) {
                requiredDistance = distance;
                resultCell = newCell.getId();
            }
        }

        return resultCell == startCell ? -1 : resultCell;
    }

    public static short getNearestCellDiagonal(AbstractMap map, short startCell, short endCell) {
        int requiredDistance = 1000;
        short resultCell = startCell;

        for (Orientation orientation : Orientation.ADJACENT) {
            Cell cell = map.getCells().get(getCellIdByOrientation(endCell, orientation, map.getWidth()));

            if (cell != null) {
                Orientation[] orientations = null;
                switch (orientation) {
                    case SOUTH_EAST:
                        orientations = new Orientation[]{Orientation.SOUTH_EAST, Orientation.NORTH_EAST, Orientation.SOUTH_WEST};
                        break;
                    case NORTH_WEST:
                        orientations = new Orientation[]{Orientation.SOUTH_WEST, Orientation.NORTH_EAST, Orientation.SOUTH_WEST};
                        break;
                    case SOUTH_WEST:
                        orientations = new Orientation[]{Orientation.SOUTH_WEST};
                        break;
                    case NORTH_EAST:
                        orientations = new Orientation[]{Orientation.NORTH_EAST};
                        break;
                }

                for (Orientation secondOrientation : orientations) {
                    Cell secondCell = map.getCells().get(getCellIdByOrientation(cell.getId(), secondOrientation, map.getWidth()));
                    if (secondCell != null) {
                        int distance = distanceBetween(map.getWidth(), startCell, secondCell.getId());
                        if (distance < requiredDistance && secondCell.isWalkable() && secondCell.isFree()) {
                            requiredDistance = distance;
                            resultCell = secondCell.getId();
                        }
                    }
                }

            }
        }

        return resultCell == startCell ? -1 : resultCell;
    }

    public static short moreFarCell(Fighter fighter) {
        AtomicInteger distance = new AtomicInteger(0);
        AtomicInteger selectedCell = new AtomicInteger(0);

        List<Fighter> enemies = fighter.getFight().otherTeam(fighter.getTeam());
        byte width = fighter.getLocation().getMap().getWidth();

        fighter.getFight().getFightMap().getCells().values().stream()
                .filter(cell -> distanceBetween(width, cell.getId(), fighter.getFightCell().getId()) <= fighter.getCurrentMovementPoint()).forEach(cell -> {
            short currentDistance = middleDistance(enemies, cell.getId(), width);
            if (currentDistance > distance.get() && cell.isFree() && cell.isWalkable()) {
                distance.set(currentDistance);
                selectedCell.set(cell.getId());
            }
        });

        return selectedCell.shortValue();
    }

    private static short middleDistance(List<Fighter> fighters, short cell, byte width) {
        return (short) (fighters.stream().mapToInt(fighter -> distanceBetween(width, cell, fighter.getFightCell().getId())).sum() / fighters.size());
    }

    public static boolean cellAroundIsOccupied(AbstractMap map, short cell) {
        return Arrays.stream(Orientation.ADJACENT).filter(orientation -> map.getCells().get(getCellIdByOrientation(cell, orientation, map.getWidth())).getCreatures().isEmpty()).count() != 4;
    }

    public static short getCellBetweenEnemy(short cellId, AbstractMap map) {
        for (Orientation orientation : Orientation.ADJACENT) {

            Cell cell = map.getCells().get(getCellIdByOrientation(cellId, orientation, map.getWidth()));

            if (cell == null)
                continue;

            if (cell.isWalkable() && cell.isFree())
                return cell.getId();
        }
        return 0;
    }

    public static short getBestDiagonalCell(short fighterCell, short targetCell, AbstractMap map) {
        AtomicInteger bestCell = new AtomicInteger(-1);
        AtomicInteger bestDistance = new AtomicInteger(60);

        map.getCells().values().stream().filter(cell -> checkLineOfSide(map, targetCell, cell.getId(), false)).forEach(cell -> {
            short distance = distanceBetween(map.getWidth(), cell.getId(), fighterCell);
            if (distance < bestDistance.get() && cell.isFree() && cell.isWalkable()) {
                bestCell.set(cell.getId());
                bestDistance.set(distance);
            }

        });
        return bestCell.shortValue();
    }

    public static void showArroundCell(AbstractMap map, short cell, int limit) {
        map.getCells().values().stream().filter(targetCell -> distanceBetween(map.getWidth(), cell, targetCell.getId()) <= limit).forEach(found -> {
            map.send(FightPacketFormatter.showCellMessage(1, found.getId()));
        });
    }

    public static boolean inSameLine(byte width, short firstCell, short secondCell) {
        return getXCoordinates(width, firstCell) == getXCoordinates(width, secondCell) || getYCoordinates(width, firstCell) == getYCoordinates(width, secondCell);
    }

    public static boolean checkLineOfSide(AbstractMap map, short firstCell, short secondCell, boolean checkDistance) {
        return distanceBetween(map.getWidth(), firstCell, secondCell) <= 2 && checkDistance ||
                new PathMaker(map, firstCell, secondCell).getDirectPath().stream().filter(cell -> !cell.allowLineOfSide()).count() < 2 &&
                        new PathMaker(map, secondCell, firstCell).getDirectPath().stream().filter(cell -> !cell.allowLineOfSide()).count() < 2;
    }


    public static short getZaapCost(GameMap first, GameMap second) {
        return (short) (10 * (Math.abs(second.getX() - first.getX()) + Math.abs(second.getY() - first.getY()) - 1));
    }


}


