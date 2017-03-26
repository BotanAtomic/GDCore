package org.graviton.game.alignment;

import lombok.Data;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.alignment.type.AlignmentType;

import java.util.stream.IntStream;

/**
 * Created by Botan on 12/11/2016 : 14:20
 */
@Data
public class Alignment {
    private AlignmentType type;
    private int honor, dishonor;
    private byte grade, alignmentLevel;
    private boolean enabled;

    private final EntityFactory entityFactory;

    public Alignment(byte id, int honor, int dishonor, boolean enabled, EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
        this.type = AlignmentType.get(id);
        this.addHonor(honor);
        this.dishonor = dishonor;
        this.alignmentLevel = 1;
        this.enabled = enabled;
    }

    public byte getId() {
        return (byte) type.ordinal();
    }

    public void changeAlignment(byte newAlignment) {
        this.type = AlignmentType.get(newAlignment);
        this.honor = 0;
        this.dishonor = 0;
        this.grade = 1;
        this.alignmentLevel = 1;
        this.enabled = false;
    }

    public void addDishonor() {
        this.dishonor++;
    }

    public void removeDishonor() {
        this.dishonor--;
    }

    private void calculateGrade() {
        if (this.type == AlignmentType.NEUTRE)
            this.grade = 0;
        else if (this.honor >= 17500)
            this.grade = 10;
        else
            IntStream.range(1, 11).forEach(i -> {
                if (this.honor >= entityFactory.getExperience((byte) i).getAlignment())
                    this.grade = (byte) i;
            });
    }

    public void addHonor(int honor) {
        this.honor += honor;
        if (entityFactory != null)
            calculateGrade();
    }

}
