package org.graviton.game.items.common;

import org.graviton.utils.Utils;

import java.util.Random;

/**
 * Created by Botan on 04/12/2016. 18:32
 */
public class Bonus {
    private static final ThreadLocal<Random> RANDOM = ThreadLocal.withInitial(() -> new Random(System.nanoTime()));

    private short round, num, add;

    public Bonus(short round, short num) {
        this.round = round;
        this.num = num;
    }

    Bonus(short round, short num, short add) {
        this.round = round;
        this.num = num;
        this.add = add;
    }

    public static Bonus parseBonus(String string) {
        return parseBonus(string, 10);
    }

    public static Bonus parseBonus(String string, int radix) {
        short a = (short) string.indexOf('d'),
                b = (short) string.indexOf('+');

        short round = Short.parseShort(string.substring(0, a), radix),
                num = b >= 0 ? Short.parseShort(string.substring(a + 1, b), radix) : Short.parseShort(string.substring(a + 1), radix),
                add = b >= 0 ? Short.parseShort(string.substring(b + 1), radix) : 0;

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

    public String toString(int radix) {
        return add > 0 ?
                (Integer.toString(round, radix) + "d" + Integer.toString(num, radix) + "+" + Integer.toString(add, radix)) :
                (Integer.toString(round, radix) + "d" + Integer.toString(num, radix));
    }

}
