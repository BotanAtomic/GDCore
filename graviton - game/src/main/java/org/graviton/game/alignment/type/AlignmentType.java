package org.graviton.game.alignment.type;

/**
 * Created by Botan on 12/11/2016 : 14:27
 */
public enum AlignmentType {
    NEUTRE,
    BONTARIEN,
    BRAKMARIEN,
    MERCENAIRE;

    public static boolean isOpposit(AlignmentType first, AlignmentType second) {
        return first == BONTARIEN && second == BRAKMARIEN || second == BONTARIEN && first == BRAKMARIEN;
    }

    public static AlignmentType get(byte id) {
        return values()[id == -1 ? 0 : id];
    }

}
