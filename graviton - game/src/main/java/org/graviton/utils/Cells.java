package org.graviton.utils;


/**
 * Created by Botan on 19/11/2016 : 11:01
 */
public final class Cells {

    public static String encode(short cell) {
        return Character.toString(Utils.HASH[(cell / 64)]) + Character.toString(Utils.HASH[((cell % 64))]);
    }

    public static short decode(String string) {
        return (short) (Utils.EXTENDED_ALPHABET.indexOf(string.charAt(0)) * 64 + Utils.EXTENDED_ALPHABET.indexOf(string.charAt(1)));
    }

    public static short getCellIdByOrientation(short cell, char orientation, byte width) {
        switch (orientation) {
            case 'a':
                return (short) (cell + 1);
            case 'b':
                return (short) (cell + width);
            case 'c':
                return (short) (cell + (width * 2 - 1));
            case 'd':
                return (short) (cell + (width - 1));
            case 'e':
                return (short) (cell - 1);
            case 'f':
                return (short) (cell - width);
            case 'g':
                return (short) (cell - (width * 2 - 1));
            case 'h':
                return (short) (cell - width + 1);
            default:
                return cell;
        }
    }

}
