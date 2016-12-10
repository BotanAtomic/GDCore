package org.graviton.game.items.common;

import javafx.util.Pair;
import org.graviton.game.statistics.common.CharacteristicType;


/**
 * Created by Botan on 03/12/2016. 21:29
 */
public enum ItemEffect {
    NONE((short) 0),
    AddRenvoiDamage((short) 107, CharacteristicType.DamageReturn),
    Heal((short) 108),
    AddAgility((short) 119, CharacteristicType.Agility),
    AddChance((short) 123, CharacteristicType.Chance),
    AddDamage((short) 112, CharacteristicType.Damage),
    AddDamagePercent((short) 138, CharacteristicType.DamagePer),
    AddCriticalHit((short) 115, CharacteristicType.CriticalHit),
    AddCriticalFailure((short) 122, CharacteristicType.CriticalFailure),
    AddDodgeAP((short) 160, CharacteristicType.DodgeActionPoints),
    AddDodgeMP((short) 161, CharacteristicType.DodgeMovementPoints),
    AddStrength((short) 118, CharacteristicType.Strength),
    AddInitiative((short) 174, CharacteristicType.Initiative),
    AddIntelligence((short) 126, CharacteristicType.Intelligence),
    AddSummons((short) 182, CharacteristicType.Summons),
    AddLife((short) 110, CharacteristicType.Life),
    AddActionPoints((short) 111, CharacteristicType.ActionPoints),
    AddMovementPoints((short) 128, CharacteristicType.MovementPoints),
    AddRangePoints((short) 117, CharacteristicType.RangePoints),
    AddPods((short) 158, CharacteristicType.Pods),
    AddProspection((short) 176, CharacteristicType.Prospection),
    AddWisdom((short) 124, CharacteristicType.Wisdom),
    AddHealPoints((short) 178, CharacteristicType.HealPoints),
    AddVitality((short) 125, CharacteristicType.Vitality),
    MultiplyDamage((short) 114),
    SubAgility((short) 154, CharacteristicType.Agility),
    SubChance((short) 152, CharacteristicType.Chance),
    SubDamage((short) 164, CharacteristicType.Damage),
    SubCriticalHit((short) 171, CharacteristicType.CriticalHit),
    SubDamageMagic((short) 172),
    TrapDamage((short) 225),
    SubDamagePhysic((short) 173, CharacteristicType.PhysicalDamage),
    SubDodgeAP((short) 162, CharacteristicType.ActionPoints),
    SubDodgeMP((short) 163, CharacteristicType.MovementPoints),
    SubStrength((short) 157, CharacteristicType.Strength),
    SubInitiative((short) 175, CharacteristicType.Initiative),
    SubIntelligence((short) 155, CharacteristicType.Intelligence),
    SubActionPoints((short) 168, CharacteristicType.ActionPoints),
    SubMovementPoints((short) 169, CharacteristicType.MovementPoints),
    SubRangePoints((short) 116, CharacteristicType.RangePoints),
    SubPods((short) 159, CharacteristicType.Pods),
    SubProspection((short) 177, CharacteristicType.Prospection),
    SubWisdom((short) 156, CharacteristicType.Wisdom),
    SubHealPoints((short) 179, CharacteristicType.HealPoints),
    SubVitality((short) 153, CharacteristicType.Vitality),

    AddReduceDamagePercentWater((short) 211, CharacteristicType.ResistancePercentWater),
    AddReduceDamagePercentEarth((short) 210, CharacteristicType.ResistancePercentEarth),
    AddReduceDamagePercentWind((short) 212, CharacteristicType.ResistancePercentWind),
    AddReduceDamagePercentFire((short) 213, CharacteristicType.ResistancePercentFire),
    AddReduceDamagePercentNeutral((short) 214, CharacteristicType.ResistancePercentNeutral),

    AddReduceDamagePercentPvPWater((short) 251, CharacteristicType.ResistancePercentPvpWater),
    AddReduceDamagePercentPvPEarth((short) 250, CharacteristicType.ResistancePercentPvpEarth),
    AddReduceDamagePercentPvPWind((short) 252, CharacteristicType.ResistancePercentPvpWind),
    AddReduceDamagePercentPvPFire((short) 253, CharacteristicType.ResistancePercentPvpFire),
    AddReduceDamagePercentPvpNeutral((short) 254, CharacteristicType.ResistancePercentPvpNeutral),

    AddReduceDamageWater((short) 241, CharacteristicType.ResistanceWater),
    AddReduceDamageEarth((short) 240, CharacteristicType.ResistanceEarth),
    AddReduceDamageWind((short) 242, CharacteristicType.ResistanceWind),
    AddReduceDamageFire((short) 243, CharacteristicType.ResistanceFire),
    AddReduceDamageNeutral((short) 244, CharacteristicType.ResistanceNeutral),

    AddReduceDamagePvPWater((short) 261, CharacteristicType.ResistancePvpWater),
    AddReduceDamagePvPEarth((short) 260, CharacteristicType.ResistancePvpEarth),
    AddReduceDamagePvPWind((short) 262, CharacteristicType.ResistancePvpWind),
    AddReduceDamagePvPFire((short) 263, CharacteristicType.ResistancePvpFire),
    AddReduceDamagePvPNeutral((short) 264, CharacteristicType.ResistancePvpNeutral),

    SubReduceDamagePercentWater((short) 216, CharacteristicType.ResistancePercentWater),
    SubReduceDamagePercentEarth((short) 215, CharacteristicType.ResistancePercentEarth),
    SubReduceDamagePercentWind((short) 217, CharacteristicType.ResistancePercentWind),
    SubReduceDamagePercentFire((short) 218, CharacteristicType.ResistancePercentFire),
    SubReduceDamagePercentNeutral((short) 219, CharacteristicType.ResistancePercentNeutral),

    SubReduceDamagePercentPvPWater((short) 255, CharacteristicType.ResistancePercentPvpWater),
    SubReduceDamagePercentPvPEarth((short) 256, CharacteristicType.ResistancePercentPvpEarth),
    SubReduceDamagePercentPvPWind((short) 257, CharacteristicType.ResistancePercentPvpWind),
    SubReduceDamagePercentPvPFire((short) 258, CharacteristicType.ResistancePercentPvpFire),
    SubReduceDamagePercentPvpNeutral((short) 259, CharacteristicType.ResistancePercentPvpNeutral),

    SubReduceDamageWater((short) 246, CharacteristicType.ResistanceWater),
    SubReduceDamageEarth((short) 245, CharacteristicType.ResistanceEarth),
    SubReduceDamageWind((short) 247, CharacteristicType.ResistanceWind),
    SubReduceDamageFire((short) 248, CharacteristicType.ResistanceFire),
    SubReduceDamageNeutral((short) 249, CharacteristicType.ResistanceNeutral),

    StolenWater((short) 91),
    StolenEarth((short) 92),
    StolenWind((short) 93),
    StolenFire((short) 94),
    StolenNeutral((short) 95),
    InflictDamageWater((short) 96),
    InflictDamageEarth((short) 97),
    InflictDamageWind((short) 98),
    InflictDamageFire((short) 99),
    InflictDamageNeutral((short) 100),

    AddSpell((short) 604),
    AddCharactForce((short) 607),
    AddCharactSagesse((short) 678),
    AddCharactChance((short) 608),
    AddCharactAgilite((short) 609),
    AddCharactVitalite((short) 610),
    AddCharactIntelligence((short) 611),
    AddCharactPoint((short) 612),
    AddSpellPoint((short) 613),

    SubActionPointsDodge((short) 101),
    SubMovementPointsDodge((short) 127),

    AddReduceDamagePhysic((short) 183),
    AddReduceDamageMagic((short) 184),

    PetLife((short) 800);

    private short value;
    private CharacteristicType characteristic = null;

    ItemEffect(short value, CharacteristicType characteristic) {
        this.value = value;
        this.characteristic = characteristic;
    }

    ItemEffect(short value) {
        this.value = value;
    }

    public static ItemEffect get(short value) {
        for (ItemEffect effect : values())
            if (effect.value == value)
                return effect;
        return null;
    }

    public short value() {
        return value;
    }

    public Pair<CharacteristicType, Boolean> convert() {
        return new Pair<>(this.characteristic, !this.name().contains("Sub"));
    }

    public boolean isWeaponEffect() {
        switch (this) {
            case StolenWater:
            case StolenEarth:
            case StolenWind:
            case StolenFire:
            case StolenNeutral:
            case InflictDamageWater:
            case InflictDamageEarth:
            case InflictDamageWind:
            case InflictDamageFire:
            case InflictDamageNeutral:
                return true;
            default:
                return false;
        }
    }
}
