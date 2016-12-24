package org.graviton.game.statistics.common;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Botan on 11/11/2016 : 21:15
 */
public enum CharacteristicType {
    Life,
    Pods,
    Prospection,
    Initiative,
    ActionPoints,
    MovementPoints,
    Strength,
    Vitality,
    Wisdom,
    Chance,
    Agility,
    Intelligence,
    RangePoints,
    Summons,
    Damage,
    PhysicalDamage,
    WeaponControl,
    DamagePer,
    HealPoints,
    TrapDamage,
    TrapDamagePer,
    DamageReturn,
    CriticalHit,
    CriticalFailure,

    DodgeActionPoints,
    DodgeMovementPoints,

    ResistanceNeutral,
    ResistancePercentNeutral,
    ResistancePvpNeutral,
    ResistancePercentPvpNeutral,

    ResistanceEarth,
    ResistancePercentEarth,
    ResistancePvpEarth,
    ResistancePercentPvpEarth,

    ResistanceWater,
    ResistancePercentWater,
    ResistancePvpWater,
    ResistancePercentPvpWater,

    ResistanceWind,
    ResistancePercentWind,
    ResistancePvpWind,
    ResistancePercentPvpWind,

    ResistanceFire,
    ResistancePercentFire,
    ResistancePvpFire,
    ResistancePercentPvpFire;

    public static List<CharacteristicType> asBuild = Arrays.asList(CharacteristicType.Strength, CharacteristicType.Vitality,
            CharacteristicType.Wisdom, CharacteristicType.Chance,
            CharacteristicType.Agility, CharacteristicType.Intelligence,
            CharacteristicType.RangePoints, CharacteristicType.Summons,
            CharacteristicType.Damage, CharacteristicType.PhysicalDamage,
            CharacteristicType.WeaponControl, CharacteristicType.DamagePer,
            CharacteristicType.HealPoints, CharacteristicType.TrapDamage,
            CharacteristicType.TrapDamagePer, CharacteristicType.DamageReturn,
            CharacteristicType.CriticalHit, CharacteristicType.CriticalFailure,
            CharacteristicType.DodgeActionPoints, CharacteristicType.DodgeMovementPoints,
            CharacteristicType.ResistanceNeutral, CharacteristicType.ResistancePercentNeutral,
            CharacteristicType.ResistancePvpNeutral, CharacteristicType.ResistancePercentPvpNeutral,
            CharacteristicType.ResistanceEarth, CharacteristicType.ResistancePercentEarth,
            CharacteristicType.ResistancePvpEarth, CharacteristicType.ResistancePercentPvpEarth,
            CharacteristicType.ResistanceWater, CharacteristicType.ResistancePercentWater,
            CharacteristicType.ResistancePvpWater, CharacteristicType.ResistancePercentPvpWater,
            CharacteristicType.ResistanceWind, CharacteristicType.ResistancePercentWind,
            CharacteristicType.ResistancePvpWind, CharacteristicType.ResistancePercentPvpWind,
            CharacteristicType.ResistanceFire, CharacteristicType.ResistancePercentFire,
            CharacteristicType.ResistancePvpFire, CharacteristicType.ResistancePercentPvpFire);

    public static CharacteristicType getBoost(byte characteristics) {
        switch (characteristics) {
            case 10:
                return Strength;
            case 11:
                return Vitality;
            case 12:
                return Wisdom;
            case 13:
                return Chance;
            case 14:
                return Agility;
            case 15:
                return Intelligence;
        }
        return null;
    }
}
