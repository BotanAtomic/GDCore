package org.graviton.game.spell.common;


import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.Buff;
import org.graviton.game.effect.enums.DamageType;
import org.graviton.game.effect.type.buff.*;
import org.graviton.game.effect.type.damage.*;
import org.graviton.game.effect.type.other.*;
import org.graviton.game.effect.type.push.PushBackEffect;
import org.graviton.game.effect.type.push.PushFearEffect;
import org.graviton.game.effect.type.push.PushFrontEffect;
import org.graviton.game.effect.type.transport.*;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.statistics.common.CharacteristicType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Botan on 24/12/2016. 18:16
 */
public enum SpellEffects {
    None(-1),

    Teleport(4, new TeleportEffect()),
    PushBack(5, (short) 500, new PushBackEffect()),
    PushFront(6, (short) 500, new PushFrontEffect()),
    Transpose_ally(9, new TranspositionEffect(true, false)),
    Transpose(7, new TranspositionEffect(false, true)),
    Transpose_all(8, new TranspositionEffect(true, true)),

    StealMP(77, (short) 1500, new StealPointEffect(CharacteristicType.MovementPoints)),
    StealLife(82, new StealEffect(DamageType.NULL, false)),
    StealAllyLife(83, new StealEffect(DamageType.NULL, true)),
    StealAP(84, (short) 1500, new StealPointEffect(CharacteristicType.ActionPoints)),

    DamageLifeWater(85, new DamageLifeEffect(DamageType.WATER)),
    DamageLifeEarth(86, new DamageLifeEffect(DamageType.EARTH)),
    DamageLifeWind(87, new DamageLifeEffect(DamageType.WIND)),
    DamageLifeFire(88, new DamageLifeEffect(DamageType.FIRE)),
    DamageLifeNeutral(89, (short) 200, new DamageLifeEffect(DamageType.NEUTRAL)),

    StealWater(91, (short) 150, new StealEffect(DamageType.WATER, false)),
    StealEarth(92, (short) 150, new StealEffect(DamageType.EARTH, false)),
    StealWind(93, (short) 150, new StealEffect(DamageType.WIND, false)),
    StealFire(94, (short) 150, new StealEffect(DamageType.FIRE, false)),
    StealNeutral(95, (short) 150, new StealEffect(DamageType.NULL, false)),

    DamageWater(96, (short) 100, new DamageEffect(DamageType.WATER)),
    DamageEarth(97, (short) 100, new DamageEffect(DamageType.EARTH)),
    DamageWind(98, (short) 100, new DamageEffect(DamageType.WIND)),
    DamageFire(99, (short) 100, new DamageEffect(DamageType.FIRE)),
    DamageNeutral(100, (short) 100, new DamageEffect(DamageType.NEUTRAL)),

    AddArmor(105, new SimpleStatisticEffect(CharacteristicType.Armor, true)),
    ReturnSpell(106, new ReturnSpellEffect()),
    AddArmorBis(265, new ArmorEffect()),

    AddReturnDamage(107, new SimpleStatisticEffect(CharacteristicType.DamageReturn, true)),
    Heal(108, new HealEffect()),
    DamageThrower(109),
    AddLife(110),
    AddAP(111, (short) 3000, new SimpleStatisticEffect(CharacteristicType.ActionPoints, true)),
    AddDamage(112, new SimpleStatisticEffect(CharacteristicType.Damage, true)),
    MultiplyDamage(114),

    AddAPBis(120, new SimpleStatisticEffect(CharacteristicType.ActionPoints, true)),
    AddAgility(119, new SimpleStatisticEffect(CharacteristicType.Agility, true)),
    AddChance(123, new SimpleStatisticEffect(CharacteristicType.Chance, true)),
    AddDamagePercent(138, new SimpleStatisticEffect(CharacteristicType.DamagePer, true)),
    AddDamageCritic(115, new SimpleStatisticEffect(CharacteristicType.CriticalHit, true)),
    AddDamageTrap(225, new SimpleStatisticEffect(CharacteristicType.TrapDamage, true)),
    AddDamageTrapPercent(225, new SimpleStatisticEffect(CharacteristicType.TrapDamagePer, true)),
    AddDamagePhysic(142, new SimpleStatisticEffect(CharacteristicType.DamagePhysic, true)),
    AddDamageMagic(143, new SimpleStatisticEffect(CharacteristicType.DamageMagic, true)),
    AddCriticalFailure(122, new SimpleStatisticEffect(CharacteristicType.CriticalFailure, true)),
    AddDodgeAP(160, new SimpleStatisticEffect(CharacteristicType.DodgeActionPoints, true)),
    AddDodgeMP(161, new SimpleStatisticEffect(CharacteristicType.DodgeMovementPoints, true)),
    AddStrength(118, new SimpleStatisticEffect(CharacteristicType.Strength, true)),
    AddInitiative(174, new SimpleStatisticEffect(CharacteristicType.Initiative, true)),
    AddIntelligence(126, new SimpleStatisticEffect(CharacteristicType.Intelligence, true)),
    AddInvocationMax(182, new SimpleStatisticEffect(CharacteristicType.Summons, true)),
    AddMP(128, (short) -1000, new SimpleStatisticEffect(CharacteristicType.MovementPoints, true)),
    AddPO(117, new SimpleStatisticEffect(CharacteristicType.RangePoints, true)),
    AddProspection(176, new SimpleStatisticEffect(CharacteristicType.Prospection, true)),
    AddWisdom(124, new SimpleStatisticEffect(CharacteristicType.Wisdom, true)),
    AddCarePoints(178, new SimpleStatisticEffect(CharacteristicType.HealPoints, true)),
    AddVitality(125, new SimpleStatisticEffect(CharacteristicType.Vitality, true)),

    SubAgility(154, new SimpleStatisticEffect(CharacteristicType.Agility, false)),
    SubChance(152, new SimpleStatisticEffect(CharacteristicType.Chance, false)),
    SubDamage(164, new SimpleStatisticEffect(CharacteristicType.Damage, false)),
    SubDamageCritic(171, new SimpleStatisticEffect(CharacteristicType.CriticalHit, false)),
    SubDamageMagic(172, new SimpleStatisticEffect(CharacteristicType.DamageMagic, false)),
    SubDamagePhysic(173, new SimpleStatisticEffect(CharacteristicType.DamagePhysic, false)),
    SubDodgeAP(162, (short) 2000, new SimpleStatisticEffect(CharacteristicType.DodgeActionPoints, false)),
    SubDodgeMP(163, (short) 2000,new SimpleStatisticEffect(CharacteristicType.DodgeMovementPoints, false)),
    SubStrength(157, new SimpleStatisticEffect(CharacteristicType.Strength, false)),
    SubInitiative(175, new SimpleStatisticEffect(CharacteristicType.Initiative, false)),
    SubIntelligence(155, new SimpleStatisticEffect(CharacteristicType.Intelligence, false)),
    SubAPDodge(101, (short) 2000, new StatisticDodgeEffect(CharacteristicType.ActionPoints)),
    SubMPDodge(127, (short) 2000, new StatisticDodgeEffect(CharacteristicType.MovementPoints)),
    SubAP(168, new SimpleStatisticEffect(CharacteristicType.ActionPoints, false)),
    SubMP(169, new SimpleStatisticEffect(CharacteristicType.MovementPoints, false)),
    SubPO(116, new SimpleStatisticEffect(CharacteristicType.RangePoints, false)),
    SubProspection(177, new SimpleStatisticEffect(CharacteristicType.Prospection, false)),
    SubWisdom(156, new SimpleStatisticEffect(CharacteristicType.Wisdom, false)),
    SubCarePoints(179, new SimpleStatisticEffect(CharacteristicType.HealPoints, false)),
    SubVitality(153, new SimpleStatisticEffect(CharacteristicType.Vitality, false)),

    Invocation(181, new InvocationEffect(false)),

    AddReduceDamagePhysic(183, new SimpleStatisticEffect(CharacteristicType.ReducePhysic, true)),
    AddReduceDamageMagic(184, new SimpleStatisticEffect(CharacteristicType.ReduceMagic, true)),

    AddReduceDamagePercentWater(211, new SimpleStatisticEffect(CharacteristicType.ResistancePercentWater, true)),
    AddReduceDamagePercentEarth(210, new SimpleStatisticEffect(CharacteristicType.ResistancePercentEarth, true)),
    AddReduceDamagePercentWind(212, new SimpleStatisticEffect(CharacteristicType.ResistancePercentWind, true)),
    AddReduceDamagePercentFire(213, new SimpleStatisticEffect(CharacteristicType.ResistancePercentFire, true)),
    AddReduceDamagePercentNeutral(214, new SimpleStatisticEffect(CharacteristicType.ResistancePercentNeutral, true)),

    AddReduceDamageWater(241, new SimpleStatisticEffect(CharacteristicType.ResistanceWater, true)),
    AddReduceDamageEarth(240, new SimpleStatisticEffect(CharacteristicType.ResistanceEarth, true)),
    AddReduceDamageWind(242, new SimpleStatisticEffect(CharacteristicType.ResistanceWind, true)),
    AddReduceDamageFire(243, new SimpleStatisticEffect(CharacteristicType.ResistanceFire, true)),
    AddReduceDamageNeutral(244, new SimpleStatisticEffect(CharacteristicType.ResistanceNeutral, true)),

    SubReduceDamagePercentWater(216, new SimpleStatisticEffect(CharacteristicType.ResistancePercentWater, false)),
    SubReduceDamagePercentEarth(215, new SimpleStatisticEffect(CharacteristicType.ResistancePercentEarth, false)),
    SubReduceDamagePercentWind(217, new SimpleStatisticEffect(CharacteristicType.ResistancePercentWind, false)),
    SubReduceDamagePercentFire(218, new SimpleStatisticEffect(CharacteristicType.ResistancePercentFire, false)),
    SubReduceDamagePercentNeutral(219, new SimpleStatisticEffect(CharacteristicType.ResistancePercentNeutral, false)),

    SubReduceDamageWater(246, new SimpleStatisticEffect(CharacteristicType.ResistanceWater, false)),
    SubReduceDamageEarth(245, new SimpleStatisticEffect(CharacteristicType.ResistanceEarth, false)),
    SubReduceDamageWind(247, new SimpleStatisticEffect(CharacteristicType.ResistanceWind, false)),
    SubReduceDamageFire(248, new SimpleStatisticEffect(CharacteristicType.ResistanceFire, false)),
    SubReduceDamageNeutral(249, new SimpleStatisticEffect(CharacteristicType.ResistanceNeutral, false)),

    Carry(50, new HoldingEffect()),
    Launch(51, new LaunchEffect()),
    ChangeSkin(149, new SkinEffect()),
    SpellBoost(293, new SpellBoostEffect()),
    UseTrap(400, new TrapEffect(false)),
    UseGlyph(401, new TrapEffect(true)),
    DoNothing(666),
    DamageLife(672, new PunishmentLifeEffect()),
    PushFear(783, new PushFearEffect()),
    AddPunishment(788, new PunishmentEffect()),
    AddState(950, new StateEffect(false)),
    LostState(951, new StateEffect(true)),
    Invisible(150, new InvisibleEffect()),

    ClearBuffs(132, (short) 2000, new ClearBuffEffect()),

    StealStrength(271, new StealStatisticEffect(CharacteristicType.Strength)),

    PassTurn(140, new PassTurnEffect()),

    Unknown_141(141),

    SetVisible(202, new VisibleEffect()),

    StealAgility(268, new StealStatisticEffect(CharacteristicType.Agility)),

    Unknown_130(130),

    Double(180, new DoubleEffect()),

    StealPO(320, new StealRangeEffect()),

    RandomAttackResult(79, new RandomAttackResultEffect()),

    Remove_damage(145, new RemoveDamageEffect()),

    Static_Invocation(185, new InvocationEffect(true)),

    Unknown_131(131),

    Unknown_136(136),

    Unknown_279(279),

    Unknown_751(751),

    Unknown_201(201),

    Unknown_750(750),

    Unknown_276(276),

    Unknown_402(402),

    DodgeAttack(987, new DodgeAttackEffect()),

    Unknown_78(78),

    Unknown_287(287),

    Unknown_286(286),

    Unknown_285(285),

    Unknown_135(135),

    Unknown_284(284),

    Unknown_405(405),

    Unknown_220(220),

    Unknown_81(81),

    Sacrifice(765, new SacrificeEffect()),

    Unknown_144(144),

    Unknown_333(333),

    Unknown_269(269),

    LifeTransfer(90, new LifeTransferEffect()),

    Unknown_787(787),

    Unknown_786(786),

    Rollback(784, new RollBackEffect()),

    Unknown_165(165),

    SubDamagePercent(186, new SimpleStatisticEffect(CharacteristicType.DamagePer, false)),

    Unknown_774(774),

    Unknown_775(775),

    Unknown_290(290),

    Unknown_772(772),

    Unknown_671(671),

    Unknown_773(773),

    Unknown_770(770),

    Unknown_771(771),

    Unknown_782(782),

    ResuscitateFighter(780, new ResuscitateFighterEffect()),

    Unknown_781(781),

    DamageSuffer(776, new DamageSufferEffect()),

    Unknown_606(606),

    Unknown_minus87(-87);

    private static final Map<Integer, SpellEffects> values = new HashMap<>();

    static {
        for (SpellEffects effect : values())
            values.put(effect.value(), effect);
    }

    private int value;
    public Effect effect;
    private short influence = 1;

    SpellEffects(int value) {
        this.value = value;
    }

    SpellEffects(int value, Effect effect) {
        this.value = value;
        this.effect = effect;
    }

    SpellEffects(int value, short influence, Effect effect) {
        this.value = value;
        this.effect = effect;
        this.influence = influence;
    }

    public static SpellEffects get(int value) {
        return values.get(value);
    }

    public void apply(Fighter fighter, Collection<Fighter> targets, Cell initialCell, SpellEffect spellEffect) {
        if (effect != null)
            effect.copy().apply(fighter, targets, initialCell, spellEffect);
    }

    public short getInfluence() {
        return this.influence;
    }

    public int value() {
        return value;
    }

    public boolean isInvocation() {
        return this == Invocation || this == Static_Invocation;
    }

    public boolean isPush() {
        return this == Teleport || this == PushFront;
    }

}
