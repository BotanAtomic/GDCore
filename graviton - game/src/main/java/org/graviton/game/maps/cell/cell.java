package org.graviton.game.maps.cell;

import lombok.Data;

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

    public Cell(short id) {
        this.id = id;
    }

    public enum MovementType {
        Unwalkable,
        Door,
        Trigger,
        Walkable,
        Paddock,
        Road;

        public static MovementType valueOf(int ordinal) {
            for (MovementType value : values()) {
                if (value.ordinal() == ordinal) return value;
            }
            return null;
        }
    }
}

