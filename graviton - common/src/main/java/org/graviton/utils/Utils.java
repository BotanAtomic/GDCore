package org.graviton.utils;

import org.apache.mina.core.buffer.IoBuffer;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
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
    private static final AtomicReference<Random> RANDOM = new AtomicReference<>(new SecureRandom());

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

    public static Properties parseDatabaseProperties(Properties baseProperties, String data) {
        return new Properties() {{
            baseProperties.keySet().stream().filter(key -> key.toString().contains(data)).forEach(selectedKey -> put(selectedKey, baseProperties.get(selectedKey)));
        }};
    }

    public static Properties parseComplexDatabaseProperties(Properties properties, String data) {
        return new Properties() {{
            properties.keySet().stream().filter(key -> key.toString().startsWith(data.concat(".dataSource"))).forEach(selectedKey ->
                    put(String.valueOf(selectedKey).split(data.concat("."))[1], properties.get(selectedKey))
            );
        }};
    }

    public static byte parseBase64Char(char c) {
        for (byte a = 0; a < HASH.length; a++)
            if (HASH[a] == c)
                return a;
        return -1;
    }

    public static List<Integer> arrayToIntList(String data, String regex, boolean sync) {
        List<Integer> result = sync ? new CopyOnWriteArrayList<>() : new ArrayList<>();
        for (String part : data.split(regex))
            if (!part.isEmpty())
                result.add(Integer.parseInt(part));
        return result;
    }

    public static List<Short> arrayToShortList(String data, String regex) {
        List<Short> result = new CopyOnWriteArrayList<>();
        for (String part : data.split(regex))
            if (!part.isEmpty())
                result.add(Short.parseShort(part));
        return result;
    }

    public static List<Byte> arrayToByteList(String data, String regex) {
        List<Byte> result = new ArrayList<>();
        for (String part : data.split(regex))
            if (!part.isEmpty())
                result.add(Byte.parseByte(part));
        return result;
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

    public static Date parseDate(String format, String source) {
        try {
            return new SimpleDateFormat(format).parse(source);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static <C> C getRandomObject(Collection<C> from) {
        return (C) from.toArray()[RANDOM.get().nextInt(from.size())];
    }

    public static int limit(int value, int maximum) {
        return value > maximum ? maximum : value < 0 ? 0 : value;
    }

    public static double limit(double value, double maximum) {
        return value > maximum ? maximum : value < 0 ? 0 : value;
    }

    public static boolean range(double value, double start, double end) {
        return value >= start && value <= end;
    }

    public static double difference(double first, double second) {
        return (first > second ? first - second : second - first);
    }

    public static List<Integer> parseZaaps(String data) {
        List<Integer> zaaps = new ArrayList<>();
        for (String zaap : data.split(";"))
            if (!zaap.isEmpty())
                zaaps.add(Integer.parseInt(zaap));
        return zaaps;
    }

    public static String parseZaaps(List<Integer> data) {
        if (data.isEmpty())
            return "";

        String zaap = "";
        for (int i : data)
            zaap += i + ";";
        return zaap.substring(0, zaap.length() - 1);
    }

    public static short[] shortSplit(String data, String regex) {
        int size = data.split(regex).length;
        short[] shortArray = new short[size];

        for (int i = 0; i < size; i++)
            shortArray[i] = Short.parseShort(data.split(regex)[i]);

        return shortArray;
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
