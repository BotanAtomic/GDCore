package org.graviton.game.mountpark;

import lombok.Data;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.creature.mount.Mount;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.jooq.Record;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.graviton.database.jooq.game.tables.MountparkData.MOUNTPARK_DATA;

/**
 * Created by Botan on 01/05/17. 17:50
 */

@Data
public class MountPark {
    private final Record record;

    private int owner;
    private int guild;


    private GameMap gameMap;
    private Cell cell, door, spawnCell;
    private long price;
    private byte size, objectSize;

    private List<Cell> objectCells = new ArrayList<>();
    private List<BreedingObject> objects = new ArrayList<>();

    private List<Mount> barn = Collections.synchronizedList(new ArrayList<>());

    public MountPark(Record record, EntityFactory entityFactory) {
        this.record = record;
        this.gameMap = entityFactory.getGameMapRepository().get(record.get(MOUNTPARK_DATA.MAP));
        this.gameMap.setMountPark(this);
    }

    public void initialize() {
        this.cell = this.gameMap.getCell(record.get(MOUNTPARK_DATA.CELL));
        this.door = this.gameMap.getCell(record.get(MOUNTPARK_DATA.DOOR_CELL));
        this.spawnCell = this.gameMap.getCell(record.get(MOUNTPARK_DATA.MOUNT_CELL));
        this.size = record.get(MOUNTPARK_DATA.SIZE);
        this.objectSize = record.get(MOUNTPARK_DATA.OBJECT_SIZE);
        this.price = record.get(MOUNTPARK_DATA.PRICE);

        this.owner = record.get(MOUNTPARK_DATA.OWNER);
        this.guild = record.get(MOUNTPARK_DATA.GUILD);

        Stream.of(record.get(MOUNTPARK_DATA.PARK_CELLS).split(";")).filter(data -> !data.isEmpty()).forEach(cellData -> this.objectCells.add(gameMap.getCell(Short.parseShort(cellData))));
    }


    final private static class BreedingObject {
        private short cell;
        private int object, owner, durability, maximumDurability;

        private BreedingObject(short cell, int object, int owner, int durability, int maximumDurability) {
            this.cell = cell;
            this.object = object;
            this.owner = owner;
            this.durability = durability;
            this.maximumDurability = maximumDurability;
        }
    }

}
