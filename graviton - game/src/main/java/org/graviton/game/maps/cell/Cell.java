package org.graviton.game.maps.cell;

import lombok.Data;
import org.graviton.game.maps.object.InteractiveObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

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

    private InteractiveObject interactiveObject;

    private Collection<Integer> creatures = new ArrayList<>();

    public Cell(short id) {
        this.id = id;
    }

    public boolean isWalkable() {
        return movementType != MovementType.Unwalkable && movementType != MovementType.Door;
    }

    public boolean isFree() {
        return this.creatures.isEmpty();
    }

    public int getFirstCreature() {
        Optional<Integer> value = this.creatures.stream().findFirst();
        return value.isPresent() ? value.get() : 0;
    }

    @Override
    public int hashCode() {
        return this.id;
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

