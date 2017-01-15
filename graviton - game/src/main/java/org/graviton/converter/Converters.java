package org.graviton.converter;

import org.graviton.game.effect.enums.DamageType;
import org.graviton.game.items.Item;
import org.graviton.game.spell.SpellView;
import org.graviton.game.statistics.common.CharacteristicType;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.graviton.utils.Utils.range;

/**
 * Created by Botan on 25/12/2016. 15:00
 */
public class Converters {

    public static Function<Collection<SpellView>, Collection<Byte>> SPELL_TO_BYTE = input -> input.stream().map(SpellView::getPosition).collect(Collectors.toList());

    public static Function<Short, Byte> COSTV = (Function<Short, Byte>) input -> {
        if (range(input, 0, 50)) return (byte) 2;
        else if (range(input, 51, 150)) return (byte) 3;
        else if (range(input, 151, 250)) return (byte) 4;
        else return (byte) 5;
    };

    public static Function<Short, Byte> COSTL = (Function<Short, Byte>) input -> {
        if (range(input, 0, 20)) return (byte) 1;
        else if (range(input, 21, 40)) return (byte) 2;
        else if (range(input, 41, 60)) return (byte) 3;
        else if (range(input, 61, 80)) return (byte) 4;
        else return (byte) 5;
    };

    public static Function<Short, Byte> COSTM = (Function<Short, Byte>) input -> {
        if (range(input, 0, 100)) return (byte) 1;
        else if (range(input, 101, 200)) return (byte) 2;
        else if (range(input, 201, 300)) return (byte) 3;
        else if (range(input, 301, 400)) return (byte) 4;
        else return (byte) 5;
    };

    public static Function<Short, Byte> COSTM0 = (Function<Short, Byte>) input -> {
        if (range(input, 0, 50)) return (byte) 1;
        else if (range(input, 51, 150)) return (byte) 2;
        else if (range(input, 151, 250)) return (byte) 3;
        else if (range(input, 251, 350)) return (byte) 4;
        else return (byte) 5;
    };


    public static Function<Short, Byte> COSTM1 = (Function<Short, Byte>) input -> {
        if (range(input, 0, 50)) return (byte) 1;
        else if (range(input, 51, 100)) return (byte) 2;
        else if (range(input, 101, 150)) return (byte) 3;
        else if (range(input, 151, 200)) return (byte) 4;
        else return (byte) 5;
    };

    public static Function<Short, Byte> COSTH = (Function<Short, Byte>) input -> {
        if (range(input, 0, 100)) return (byte) 3;
        else if (range(input, 101, 150)) return (byte) 4;
        else return (byte) 5;
    };

    public static Function<DamageType, CharacteristicType> DAMAGE_TO_CHARACTERISTIC = input -> {
        switch (input) {
            case WIND:
                return CharacteristicType.Agility;
            case NEUTRAL:
            case EARTH:
                return CharacteristicType.Strength;
            case FIRE:
                return CharacteristicType.Intelligence;
            case WATER:
                return CharacteristicType.Chance;
            default:
                return CharacteristicType.Empty;
        }
    };

    public static Function<DamageType, CharacteristicType> DAMAGE_TO_RESISTANCE_P = input -> {
        switch (input) {
            case WIND:
                return CharacteristicType.ResistancePercentWind;
            case NEUTRAL:
                return CharacteristicType.ResistancePercentNeutral;
            case EARTH:
                return CharacteristicType.ResistancePercentEarth;
            case FIRE:
                return CharacteristicType.ResistancePercentFire;
            case WATER:
                return CharacteristicType.ResistancePercentWater;
            default:
                return null;
        }
    };

    public static Function<DamageType, CharacteristicType> DAMAGE_TO_RESISTANCE = input -> {
        switch (input) {
            case WIND:
                return CharacteristicType.ResistanceWind;
            case NEUTRAL:
                return CharacteristicType.ResistanceNeutral;
            case EARTH:
                return CharacteristicType.ResistanceEarth;
            case FIRE:
                return CharacteristicType.ResistanceFire;
            case WATER:
                return CharacteristicType.ResistanceWater;
            default:
                return null;
        }
    };

    public static Function<Item, Short> ITEM_TO_ID = input -> input.getTemplate().getId();


}
