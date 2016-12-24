package org.graviton.game.breeds;

import com.google.common.base.Function;

import static org.graviton.utils.Utils.range;

/**
 * Created by Botan on 24/12/2016. 14:21
 */
public class Converter {

    public static Function<Short, Byte> VERY_LITTLE = (Function<Short, Byte>) input -> {
        if (range(input, 0, 50)) return (byte) 2;
        else if (range(input, 51, 150)) return (byte) 3;
        else if (range(input, 151, 250)) return (byte) 4;
        else return (byte) 5;
    };

    public static Function<Short, Byte> LITTLE = (Function<Short, Byte>) input -> {
        if (range(input, 0, 20)) return (byte) 1;
        else if (range(input, 21, 40)) return (byte) 2;
        else if (range(input, 41, 60)) return (byte) 3;
        else if (range(input, 61, 80)) return (byte) 4;
        else return (byte) 5;
    };

    public static Function<Short, Byte> MEDIUM = (Function<Short, Byte>) input -> {
        if (range(input, 0, 100)) return (byte) 1;
        else if (range(input, 101, 200)) return (byte) 2;
        else if (range(input, 201, 300)) return (byte) 3;
        else if (range(input, 301, 400)) return (byte) 4;
        else return (byte) 5;
    };

    public static Function<Short, Byte> MEDIUM0 = (Function<Short, Byte>) input -> {
        if (range(input, 0, 50)) return (byte) 1;
        else if (range(input, 51, 150)) return (byte) 2;
        else if (range(input, 151, 250)) return (byte) 3;
        else if (range(input, 251, 350)) return (byte) 4;
        else return (byte) 5;
    };


    public static Function<Short, Byte> MEDIUM1 = (Function<Short, Byte>) input -> {
        if (range(input, 0, 50)) return (byte) 1;
        else if (range(input, 51, 100)) return (byte) 2;
        else if (range(input, 101, 150)) return (byte) 3;
        else if (range(input, 151, 200)) return (byte) 4;
        else return (byte) 5;
    };

    public static Function<Short, Byte> HARD = (Function<Short, Byte>) input -> {
        if (range(input, 0, 100)) return (byte) 3;
        else if (range(input, 101, 150)) return (byte) 4;
        else return (byte) 5;
    };


}
