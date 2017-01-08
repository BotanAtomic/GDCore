package org.graviton.game.items.common;

import org.graviton.utils.Utils;

/**
 * Created by Botan on 04/12/2016. 18:32
 */
public class Bonus {
    private short round, num, add;

    private Bonus(short round, short num, short add) {
        this.round = round;
        this.num = num;
        this.add = add;
    }

    public static Bonus parseBonus(String string) {
        if (string.isEmpty())
            return new Bonus((short) 0, (short) 0, (short) 0);

        short a = (short) string.indexOf('d'),
                b = (short) string.indexOf('+');

        short round = Short.parseShort(string.substring(0, a), 10);
        short num = b >= 0 ? Short.parseShort(string.substring(a + 1, b), 10) : Short.parseShort(string.substring(a + 1), 10);
        short add = b >= 0 ? Short.parseShort(string.substring(b + 1), 10) : 0;

        return new Bonus(round, num, add);
    }

    public short min() {
        return (short) (add == 1 ? add : add + 1);
    }

    public short max() {
        return (short) (round * num + add);
    }

    public short random() {
        int min = min();
        int max = max();
        return (short) Utils.random(min < max ? min : max, min > max ? min : max);
    }

    public String toString() {
        return add > 0 ?
                (Integer.toString(round, 10) + "d" + Integer.toString(num, 10) + "+" + Integer.toString(add, 10)) :
                (Integer.toString(round, 10) + "d" + Integer.toString(num, 10));
    }

}
