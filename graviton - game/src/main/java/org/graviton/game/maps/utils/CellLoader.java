package org.graviton.game.maps.utils;

import org.graviton.database.entity.EntityFactory;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.cell.Cell.MovementType;
import org.graviton.game.maps.cell.Trigger;
import org.graviton.game.maps.fight.FightMap;
import org.graviton.game.maps.object.InteractiveObjectTemplate;
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

    public static Map<Short, Cell> parse(GameMap gameMap, String data, EntityFactory entityFactory, boolean fight) {
        Map<Short, Cell> cells = new ConcurrentHashMap<>();

        for (int i = 0; i < data.length(); i += 10) {
            Cell cell = new Cell((short) (i / 10));
            initCell(gameMap, cell, data.substring(i, i + 10), entityFactory, fight);
            cells.put((short) (i / 10), cell);
        }
        return cells;
    }

    private static void initCell(GameMap gameMap, Cell cell, String data, EntityFactory entityFactory, boolean fight) {
        int[] hashCodes = new int[10];

        for (int i = 0; i < 10; ++i)
            hashCodes[i] = Utils.EXTENDED_ALPHABET.indexOf(data.charAt(i));

        boolean los = (hashCodes[0] & 1) == 1;
        int groundLevel = hashCodes[1] & 15;
        int movementType = (hashCodes[2] & 56) >> 3;
        int groundSlope = (hashCodes[4] & 60) >> 2;

        int interactiveObject = ((hashCodes[0] & 2) << 12) + ((hashCodes[7] & 1) << 12) + (hashCodes[8] << 6) + hashCodes[9];

        if (((hashCodes[7] & 2) >> 1) != 0 && !fight) {
            InteractiveObjectTemplate interactiveObjectTemplate = entityFactory.getInteractiveObject(interactiveObject);
            if (interactiveObjectTemplate != null)
                cell.setInteractiveObject(entityFactory.getInteractiveObject(interactiveObject).newInteractiveObject(gameMap, cell.getId(), entityFactory.getScheduler()));
            else
                System.err.println("Undefined interactive object " + interactiveObject);
        }

        cell.setLineOfSight(los);
        cell.setGroundLevel(groundLevel);
        cell.setMovementType(MovementType.valueOf(movementType));
        cell.setGroundSlope(groundSlope);
    }

    public static List<Cell> getFightCells(FightMap fightMap, String places, FightSide team) {
        List<Cell> cells = new ArrayList<>();
        String data = places.split("\\|")[team.ordinal()];

        for (int i = 0; i < data.length(); i += 2)
            cells.add(fightMap.getCells().get((short) ((Utils.parseBase64Char(data.charAt(i)) << 6) + Utils.parseBase64Char(data.charAt(i + 1)))));

        return cells;
    }
}
