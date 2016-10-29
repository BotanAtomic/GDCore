package org.graviton.utils;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * Created by Botan on 29/10/2016 : 23:10
 */
public class StringUtils {
    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final AtomicReference<Random> RAND = new AtomicReference<>(new Random(System.nanoTime()));

    public static char random() {
        return ALPHABET.charAt(RAND.get().nextInt(ALPHABET.length()));
    }

    public static String generateKey() {
        StringBuilder builder = new StringBuilder(32);
        IntStream.range(0, 32).forEach(value -> builder.append(random()));
        return builder.toString();
    }

}
