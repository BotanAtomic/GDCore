package org.graviton.game.alignement.type;

/**
 * Created by Botan on 12/11/2016 : 14:27
 */
public enum AlignementType {
    NEUTRE,
    BONTARIEN,
    BRAKMARIEN,
    MERCENAIRE;

    public static AlignementType get(byte id) {
        return values()[id];
    }
}
