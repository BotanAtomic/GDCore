package org.graviton.utils;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * Created by Botan on 29/10/2016 : 23:10
 */
public class StringUtils {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final AtomicReference<Random> RAND = new AtomicReference<>(new Random(System.nanoTime()));
    private static final char[] HASH = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};


    public static char random() {
        return ALPHABET.charAt(RAND.get().nextInt(ALPHABET.length()));
    }

    public static String generateKey() {
        StringBuilder builder = new StringBuilder(32);
        IntStream.range(0, 32).forEach(value -> builder.append(random()));
        return builder.toString();
    }

    public static String encryptPassword(String password, String key) {
        byte i = (byte) HASH.length;
        StringBuilder newPassword = new StringBuilder("#1");

        for (int y = 0; y < password.length(); y++) {
            char c1 = password.charAt(y);
            char c2 = key.charAt(y);
            newPassword.append(HASH[(int) ((Math.floor(c1 / 16) + c2 % i) % i)]).append(HASH[(c1 % 16 + c2 % i) % i]);
        }
        return newPassword.toString();
    }

}
