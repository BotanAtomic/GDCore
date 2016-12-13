package org.graviton.game.maps.utils;

import org.graviton.game.fight.common.FightSide;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.cell.Trigger;
import org.graviton.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Botan on 13/11/2016 : 18:04
 */
public class CellLoader {

    public static Map<Short, Trigger> parseTrigger(String data) {
        Map<Short, Trigger> triggers = new ConcurrentHashMap<>();

        if (data.isEmpty())
            return triggers;

        for (String information : data.split(";")) {
            String[] metaData = information.split(",");

            if (metaData.length < 3)
                continue;

            triggers.put(Short.parseShort(metaData[0]), new Trigger(Integer.parseInt(metaData[1]), Short.parseShort(metaData[2])));
        }
        return triggers;
    }

    public static Map<Short, Cell> parse(String data) {
        Map<Short, Cell> cells = new ConcurrentHashMap<>();

        for (int i = 0; i < data.length(); i += 10) {
            Cell cell = new Cell((short) (i / 10));
            initCell(cell, data.substring(i, i + 10));
            cells.put((short) (i / 10), cell);
        }
        return cells;
    }

    private static void initCell(Cell cell, String data) {
        int[] hashCodes = new int[10];

        for (int i = 0; i < 10; ++i)
            hashCodes[i] = Utils.EXTENDED_ALPHABET.indexOf(data.charAt(i));

        boolean los = (hashCodes[0] & 1) == 1;
        int groundLevel = hashCodes[1] & 15;
        int movementType = (hashCodes[2] & 56) >> 3;
        int groundSlope = (hashCodes[4] & 60) >> 2;
        cell.setLineOfSight(los);
        cell.setGroundLevel(groundLevel);
        cell.setMovementType(Cell.MovementType.valueOf(movementType));
        cell.setGroundSlope(groundSlope);
    }

    public static List<Cell> getFightCells(GameMap gameMap, FightSide team) {
        List<Cell> cells = new ArrayList<>();
        String data = gameMap.getPlaces().split("\\|")[team.ordinal()];

        for (int i = 0; i < data.length(); i += 2)
            cells.add(gameMap.getCells().get((short) ((Utils.parseBase64Char(data.charAt(i)) << 6) + Utils.parseBase64Char(data.charAt(i + 1)))));

        return cells;
    }
}
