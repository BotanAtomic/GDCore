package org.graviton.game.statistics.common;

import com.google.common.collect.ImmutableList;


/**
 * Created by Botan on 11/11/2016 : 21:15
 */
public enum CharacteristicType {
    Empty,
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
    DamagePhysic,
    DamageMagic,
    MultiplyDamage,
    PhysicalDamage,
    ReducePhysic,
    ReduceMagic,
    WeaponControl,
    DamagePer,
    HealPoints,
    TrapDamage,
    TrapDamagePer,
    DamageReturn,
    CriticalHit,
    CriticalFailure,

    Armor,
    ArmorFire,
    ArmorWind,
    ArmorWater,

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

    public static final ImmutableList<CharacteristicType> asBuild = ImmutableList.of(Strength, Vitality,
            Wisdom, Chance,
            Agility, Intelligence,
            RangePoints, Summons,
            Damage, PhysicalDamage,
            WeaponControl, DamagePer,
            HealPoints, TrapDamage,
            TrapDamagePer, DamageReturn,
            CriticalHit, CriticalFailure,
            DodgeActionPoints, DodgeMovementPoints,
            ResistanceNeutral, ResistancePercentNeutral,
            ResistancePvpNeutral, ResistancePercentPvpNeutral,
            ResistanceEarth, ResistancePercentEarth,
            ResistancePvpEarth, ResistancePercentPvpEarth,
            ResistanceWater, ResistancePercentWater,
            ResistancePvpWater, ResistancePercentPvpWater,
            ResistanceWind, ResistancePercentWind,
            ResistancePvpWind, ResistancePercentPvpWind,
            ResistanceFire, ResistancePercentFire,
            ResistancePvpFire, ResistancePercentPvpFire);

    public final static ImmutableList<CharacteristicType> base = ImmutableList.of(Strength, Vitality, Wisdom, Chance, Agility, Intelligence);


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
