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
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final AtomicReference<Random> RANDOM = new AtomicReference<>(new Random(System.nanoTime()));
    private static final char[] HASH = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};


    private static char random() {
        return ALPHABET.charAt(RANDOM.get().nextInt(ALPHABET.length()));
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

}
