package org.graviton.utils;

import org.apache.mina.core.buffer.IoBuffer;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * Created by Botan on 29/10/2016 : 23:10
 */
public class StringUtils {
    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    public static final String EXTENDED_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
    public static final String VOWELS = "aeiouy";
    public static final String CONSONANTS = "bcdfghjkmnpqrstvwxz";
    public static final char[] HASH = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};
    private static final AtomicReference<Random> RANDOM = new AtomicReference<>(new Random(System.nanoTime()));

    public static String randomPseudo() {
        boolean vowels = RANDOM.get().nextBoolean();
        int length = RANDOM.get().nextInt(4) + 4;
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            if (vowels) builder.append(randomVowels());
            else builder.append(randomConsonants());
            vowels = RANDOM.get().nextBoolean();
        }
        return capitalize(builder);
    }

    public static String capitalize(StringBuilder builder) {
        return Character.toUpperCase(builder.charAt(0)) + builder.substring(1);
    }

    public static char randomVowels() {
        return VOWELS.charAt(RANDOM.get().nextInt(VOWELS.length()));
    }

    public static char randomConsonants() {
        return CONSONANTS.charAt(RANDOM.get().nextInt(CONSONANTS.length()));
    }

    private static char random() {
        return ALPHABET.charAt(RANDOM.get().nextInt(ALPHABET.length()));
    }

    public static String generateKey() {
        StringBuilder builder = new StringBuilder(32);
        IntStream.range(0, 32).forEach(value -> builder.append(random()));
        return builder.toString();
    }

    public static byte parseBase64Char(char c) {
        for (byte a = 0; a < HASH.length; a++)
            if (HASH[a] == c)
                return a;
        return -1;
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

    public static IoBuffer stringToBuffer(String packet) {
        return IoBuffer.allocate(2048).put(packet.getBytes()).flip();
    }

    public static String bufferToString(Object buffer) {
        try {
            return IoBuffer.allocate(2048).put((IoBuffer) buffer).flip().getString(Charset.forName("UTF-8").newDecoder());
        } catch (CharacterCodingException e) {
            return "undefined";
        }
    }

    public static short stringToShort(String header) {
        return (short) ((header.charAt(0) - header.charAt(1)) * (header.charAt(1) + header.charAt(0)));
    }

    public static int[] parseColors(String data, String regex) {
        String[] colors = data.split(regex);
        return new int[]{Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2])};
    }

    public static String parseColors(int[] colors) {
        return colors[0] + ";" + colors[1] + ";" + colors[2];
    }

    public static String toHex(int value) {
        return (value != -1 ? Integer.toHexString(value) : "-1");
    }

    public static int random(int minimum, int maximum) {
        return minimum + RANDOM.get().nextInt() % (maximum - minimum + 1);
    }
}
