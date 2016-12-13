package org.graviton.game.maps.cell;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Botan on 12/11/2016 : 18:17
 */
@Data
public class Cell {
    private short id;
    private boolean lineOfSight;
    private MovementType movementType;
    private int groundLevel;
    private int groundSlope;

    private Collection<Integer> creatures = new ArrayList<>();

    public Cell(short id) {
        this.id = id;
    }

    public boolean isWalkable() {
        return movementType != MovementType.Unwalkable;
    }

    public boolean isFree() {
        return this.creatures.isEmpty();
    }

    public enum MovementType {
        Unwalkable,
        Door,
        Trigger,
        Walkable,
        Paddock,
        Road;

        public static MovementType valueOf(int ordinal) {
            if (ordinal > values().length - 1)
                ordinal = values().length - 1;
            return values()[ordinal];
        }
    }
}

