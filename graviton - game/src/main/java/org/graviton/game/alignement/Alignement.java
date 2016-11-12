package org.graviton.game.alignement;

import lombok.Data;
import org.graviton.game.alignement.type.AlignementType;

/**
 * Created by Botan on 12/11/2016 : 14:20
 */
@Data
public class Alignement {
    private AlignementType type;
    private int honor, dishonor;
    private byte grade, alignementLevel;
    private boolean enabled;

    public Alignement(byte id, int honor, int dishonor, boolean enabled) {
        this.type = AlignementType.get(id);
        this.honor = honor;
        this.dishonor = dishonor;
        this.grade = 1; //TODO : calcul grade
        this.alignementLevel = 1; //TODO : pvp
        this.enabled = enabled;
    }

    public byte getId() {
        return (byte) type.ordinal();
    }

}
