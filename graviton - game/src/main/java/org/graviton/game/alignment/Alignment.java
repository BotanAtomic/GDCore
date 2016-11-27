package org.graviton.game.alignment;

import lombok.Data;
import org.graviton.game.alignment.type.AlignmentType;

/**
 * Created by Botan on 12/11/2016 : 14:20
 */
@Data
public class Alignment {
    private AlignmentType type;
    private int honor, dishonor;
    private byte grade, alignmentLevel;
    private boolean enabled;

    public Alignment(byte id, int honor, int dishonor, boolean enabled) {
        this.type = AlignmentType.get(id);
        this.honor = honor;
        this.dishonor = dishonor;
        this.grade = 1; //TODO : calcul grade
        this.alignmentLevel = 1; //TODO : pvp
        this.enabled = enabled;
    }

    public byte getId() {
        return (byte) type.ordinal();
    }

}
