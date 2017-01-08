package org.graviton.utils;

import org.apache.mina.core.buffer.IoBuffer;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * Created by Botan on 29/10/2016 : 23:10
 */
public class Utils {
    public static final String EXTENDED_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
    public static final char[] HASH = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final AtomicReference<Random> RANDOM = new AtomicReference<>(new Random(System.nanoTime()));

    public static String randomPseudo() {
        return NameGenerator.generateName();
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
        if (maximum <= 0)
            return 0;
        return ThreadLocalRandom.current().nextInt(minimum, maximum + 1);
    }

    public static String parsePosition(String position) {
        String[] data = position.split(",");
        return data[0] + ',' + data[1];
    }

    public static byte getNextPosition(Collection<Byte> places) {
        AtomicInteger nextPosition = new AtomicInteger(-1);

        for (int i = 1; i < 24; i++) {
            if (!places.contains((byte) i)) {
                nextPosition.set(i);
                break;
            }
        }

        return (byte) nextPosition.get();
    }


    public static <C> C getRandomObject(Collection<C> from) {
        return (C) from.toArray()[RANDOM.get().nextInt(from.size())];
    }

    public static int limit(int value, int maximum) {
        return value > maximum ? maximum : value < 0 ? 0 : value;
    }

    public static boolean range(short value, int start, int end) {
        return value >= start && value <= end;
    }

    static class NameGenerator {
        private static String[] BEGINNING = {"Kr", "Ca", "Ra", "Mrok", "Cru",
                "Ray", "Bre", "Zed", "Drak", "Mor", "Jag", "Mer", "Jar", "Mjol",
                "Zork", "Mad", "Cry", "Zur", "Creo", "Azak", "Azur", "Rei", "Cro",
                "Mar", "Luk"};
        private static String[] MIDDLE = {"air", "ir", "mi", "sor", "mee", "clo",
                "red", "cra", "ark", "arc", "miri", "lori", "cres", "mur", "zer",
                "marac", "zoir", "slamar", "salmar", "urak"};
        private static String[] END = {"d", "ed", "ark", "arc", "es", "er", "der",
                "tron", "med", "ure", "zur", "cred", "mur"};


        static String generateName() {
            return BEGINNING[RANDOM.get().nextInt(BEGINNING.length)] + MIDDLE[RANDOM.get().nextInt(MIDDLE.length)] + END[RANDOM.get().nextInt(END.length)];
        }
    }
}
