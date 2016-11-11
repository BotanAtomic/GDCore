package org.graviton.game.statistics.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    private static Map<Integer, CharacteristicType> values = new HashMap<>();

    public static void load() {
        for (CharacteristicType value : values())
            values.put(value.ordinal(), value);

        values = Collections.unmodifiableMap(values);
    }
}
