package org.graviton.game.spell.common;


import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.type.DamageSufferBuff;
import org.graviton.game.effect.buff.type.RandomAttackResultBuff;
import org.graviton.game.effect.enums.DamageType;
import org.graviton.game.effect.type.buff.*;
import org.graviton.game.effect.type.damage.*;
import org.graviton.game.effect.type.other.LifeTransferEffect;
import org.graviton.game.effect.type.other.TrapEffect;
import org.graviton.game.effect.type.other.VisibleEffect;
import org.graviton.game.effect.type.push.PushBackEffect;
import org.graviton.game.effect.type.push.PushFearEffect;
import org.graviton.game.effect.type.push.PushFrontEffect;
import org.graviton.game.effect.type.transport.TeleportEffect;
import org.graviton.game.effect.type.transport.TranspositionEffect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Botan on 24/12/2016. 18:16
 */
public enum SpellEffects {
    None(-1),

    Teleport(4, new TeleportEffect()),
    PushBack(5, new PushBackEffect()),
    PushFront(6, new PushFrontEffect()),
    Transpose_ally(9, new TranspositionEffect(true, false)),
    Transpose(7, new TranspositionEffect(false, true)),
    Transpose_all(8, new TranspositionEffect(true, true)),

    StealMP(77, new StealPointEffect(CharacteristicType.MovementPoints)),
    StealLife(82, new StealEffect(DamageType.NULL, false)),
    StealAllyLife(83, new StealEffect(DamageType.NULL, true)),
    StealAP(84, new StealPointEffect(CharacteristicType.ActionPoints)),

    DamageLifeWater(85),
    DamageLifeEarth(86),
    DamageLifeWind(87),
    DamageLifeFire(88),
    DamageLifeNeutral(89, new DamageLifeEffect(DamageType.NEUTRAL)),
    StealWater(91, new StealEffect(DamageType.WATER, false)),
    StealEarth(92, new StealEffect(DamageType.EARTH, false)),
    StealWind(93, new StealEffect(DamageType.WIND, false)),
    StealFire(94, new StealEffect(DamageType.FIRE, false)),
    StealNeutral(95),
    DamageWater(96, new DamageEffect(DamageType.WATER)),
    DamageEarth(97, new DamageEffect(DamageType.EARTH)),
    DamageWind(98, new DamageEffect(DamageType.WIND)),
    DamageFire(99, new DamageEffect(DamageType.FIRE)),
    DamageNeutral(100, new DamageEffect(DamageType.NEUTRAL)),
    AddArmor(105, new SimpleStatisticEffect(CharacteristicType.Armor, true)),
    Unknown_106(106),
    AddArmorBis(265),

    AddReturnDamage(107, new SimpleStatisticEffect(CharacteristicType.DamageReturn, true)),
    Heal(108, new HealEffect()),
    DamageThrower(109),
    AddLife(110),
    AddAP(111, new SimpleStatisticEffect(CharacteristicType.ActionPoints, true)),
    AddDamage(112, new SimpleStatisticEffect(CharacteristicType.Damage, true)),
    MultiplyDamage(114),

    AddAPBis(120, new SimpleStatisticEffect(CharacteristicType.ActionPoints, true)),
    AddAgility(119, new SimpleStatisticEffect(CharacteristicType.Agility, true)),
    AddChance(123, new SimpleStatisticEffect(CharacteristicType.Chance, true)),
    AddDamagePercent(138, new SimpleStatisticEffect(CharacteristicType.DamagePer, true)),
    AddDamageCritic(115, new SimpleStatisticEffect(CharacteristicType.CriticalHit, true)),
    AddDamageTrap(225, new SimpleStatisticEffect(CharacteristicType.TrapDamage, true)),
    AddDamageTrapPercent(225, new SimpleStatisticEffect(CharacteristicType.TrapDamagePer, true)),
    AddDamagePhysic(142),
    AddDamageMagic(143),
    AddCriticalFailure(122, new SimpleStatisticEffect(CharacteristicType.CriticalFailure, true)),
    AddDodgeAP(160, new SimpleStatisticEffect(CharacteristicType.DodgeActionPoints, true)),
    AddDodgeMP(161, new SimpleStatisticEffect(CharacteristicType.DodgeMovementPoints, true)),
    AddStrength(118, new SimpleStatisticEffect(CharacteristicType.Strength, true)),
    AddInitiative(174, new SimpleStatisticEffect(CharacteristicType.Initiative, true)),
    AddIntelligence(126, new SimpleStatisticEffect(CharacteristicType.Intelligence, true)),
    AddInvocationMax(182, new SimpleStatisticEffect(CharacteristicType.Summons, true)),
    AddMP(128, new SimpleStatisticEffect(CharacteristicType.MovementPoints, true)),
    AddPO(117, new SimpleStatisticEffect(CharacteristicType.RangePoints, true)),
    AddPods(158), //useless ?
    AddProspection(176, new SimpleStatisticEffect(CharacteristicType.Prospection, true)),
    AddWisdom(124, new SimpleStatisticEffect(CharacteristicType.Wisdom, true)),
    AddCarePoints(178, new SimpleStatisticEffect(CharacteristicType.HealPoints, true)),
    AddVitality(125, new SimpleStatisticEffect(CharacteristicType.Vitality, true)),

    SubAgility(154, new SimpleStatisticEffect(CharacteristicType.Agility, false)),
    SubChance(152, new SimpleStatisticEffect(CharacteristicType.Chance, false)),
    SubDamage(164, new SimpleStatisticEffect(CharacteristicType.Damage, false)),
    SubDamageCritic(171, new SimpleStatisticEffect(CharacteristicType.CriticalHit, false)),
    SubDamageMagic(172),
    SubDamagePhysic(173),
    SubDodgeAP(162, new SimpleStatisticEffect(CharacteristicType.DodgeActionPoints, false)),
    SubDodgeMP(163, new SimpleStatisticEffect(CharacteristicType.DodgeMovementPoints, false)),
    SubStrength(157, new SimpleStatisticEffect(CharacteristicType.Strength, false)),
    SubInitiative(175, new SimpleStatisticEffect(CharacteristicType.Initiative, false)),
    SubIntelligence(155, new SimpleStatisticEffect(CharacteristicType.Intelligence, false)),
    SubAPDodge(101, new StatisticDodgeEffect(CharacteristicType.ActionPoints)),
    SubMPDodge(127, new StatisticDodgeEffect(CharacteristicType.MovementPoints)),
    SubAP(168, new SimpleStatisticEffect(CharacteristicType.ActionPoints, false)),
    SubMP(169, new SimpleStatisticEffect(CharacteristicType.MovementPoints, false)),
    SubPO(116, new SimpleStatisticEffect(CharacteristicType.RangePoints, false)),
    SubPods(159), //useless ?
    SubProspection(177, new SimpleStatisticEffect(CharacteristicType.Prospection, false)),
    SubWisdom(156, new SimpleStatisticEffect(CharacteristicType.Wisdom, false)),
    SubCarePoints(179, new SimpleStatisticEffect(CharacteristicType.HealPoints, false)),
    SubVitality(153, new SimpleStatisticEffect(CharacteristicType.Vitality, false)),

    Invocation(181),

    AddReduceDamagePhysic(183, new SimpleStatisticEffect(CharacteristicType.ReducePhysic, true)),
    AddReduceDamageMagic(184, new SimpleStatisticEffect(CharacteristicType.ReduceMagic, true)),

    AddReduceDamagePercentWater(211),
    AddReduceDamagePercentEarth(210),
    AddReduceDamagePercentWind(212),
    AddReduceDamagePercentFire(213),
    AddReduceDamagePercentNeutral(214),
    AddReduceDamagePercentPvPWater(251),
    AddReduceDamagePercentPvPEarth(250),
    AddReduceDamagePercentPvAPir(252),
    AddReduceDamagePercentPvPFire(253),
    AddReduceDamagePercentPvpNeutral(254),

    AddReduceDamageWater(241),
    AddReduceDamageEarth(240),
    AddReduceDamageWind(242),
    AddReduceDamageFire(243),
    AddReduceDamageNeutral(244),
    AddReduceDamagePvPWater(261),
    AddReduceDamagePvPEarth(260),
    AddReduceDamagePvAPir(262),
    AddReduceDamagePvPFire(263),
    AddReduceDamagePvPNeutral(264),

    SubReduceDamagePercentWater(216),
    SubReduceDamagePercentEarth(215),
    SubReduceDamagePercentWind(217),
    SubReduceDamagePercentFire(218),
    SubReduceDamagePercentNeutral(219),
    SubReduceDamagePercentPvPWater(255),
    SubReduceDamagePercentPvPEarth(256),
    SubReduceDamagePercentPvAPir(257),
    SubReduceDamagePercentPvPFire(258),
    SubReduceDamagePercentPvpNeutral(259),
    SubReduceDamageWater(246),
    SubReduceDamageEarth(245),
    SubReduceDamageWind(247),
    SubReduceDamageFire(248),
    SubReduceDamageNeutral(249),

    Carry(50),
    Launch(51),
    ChangeSkin(149, new SkinEffect()),
    SpellBoost(293, new SpellBoostEffect()),
    UseTrap(400, new TrapEffect()),
    UseGlyph(401),
    DoNothing(666),
    DamageLife(672, new PunishmentLifeEffect()),
    PushFear(783, new PushFearEffect()),
    AddPunishment(788, new PunishmentEffect()),
    AddState(950, new StateEffect()),
    LostState(951),
    Invisible(150, new InvisibleEffect()),

    ClearBuffs(132, (fighter, targets, selectedCell, effect) -> targets.forEach(target -> {
        target.getFight().send(FightPacketFormatter.actionMessage((short) effect.getType().value(), fighter.getId(), target.getId()));
        target.clearBuffs();
    })),

    AddSpell(604),

    AddCharacteristicStrength(607),

    AddCharacteristicWisdom(678),

    AddCharacteristicChance(608),

    AddCharacteristicAgility(609),

    AddCharacteristicVitality(610),

    AddCharacteristicIntelligence(611),

    AddCharacteristicPoint(612),

    AddSpellPoint(613),

    LastEat(808),

    MountOwner(995),

    LivingGfxId(970),

    LivingMood(971),

    LivingSkin(972),

    LivingType(973),

    LivingXp(974),

    CanBeExchange(983),

    Incarnation(669),

    StealStrength(271, new StealStatisticEffect(CharacteristicType.Strength)),

    PassTurn(140, (fighter, targets, selectedCell, effect) -> targets.forEach(Fighter::passTurn)),

    Unknown_141(141),

    SetVisible(202, new VisibleEffect()),

    StealAgility(268, new StealStatisticEffect(CharacteristicType.Agility)),

    Unknown_130(130),

    Unknown_180(180),

    StealPO(320, new StealRangeEffect()),

    RandomAttackResult(79, (fighter, targets, selectedCell, effect) -> targets.forEach(target -> new

            RandomAttackResultBuff(target, effect))),

    Remove_damage(145, new RemoveDamageEffect()),

    Unknown_185(185),

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

    Unknown_784(784),

    Unknown_165(165),

    Unknown_186(186),

    Unknown_774(774),

    Unknown_775(775),

    Unknown_290(290),

    Unknown_772(772),

    Unknown_671(671),

    Unknown_773(773),

    Unknown_770(770),

    Unknown_771(771),

    Unknown_782(782),

    Unknown_780(780),

    Unknown_781(781),

    DamageSuffer(776, (fighter, targets, selectedCell, effect) -> targets.forEach(target -> new

            DamageSufferBuff(target, effect))),

    Unknown_606(606),

    Unknown_minus87(-87);

    private final static Map<Integer, SpellEffects> values = new HashMap<>();

    static {
        for (SpellEffects effect : values())
            values.put(effect.value(), effect);
    }

    private int value;
    private Effect effect;

    SpellEffects(int value) {
        this.value = value;
    }

    SpellEffects(int value, Effect effect) {
        this.value = value;
        this.effect = effect;
    }

    public static SpellEffects get(int value) {
        return values.get(value);
    }

    public void apply(Fighter fighter, Collection<Fighter> targets, Cell initialCell, SpellEffect spellEffect) {
        if (effect != null)
            effect.apply(fighter, targets, initialCell, spellEffect);
    }

    public int value() {
        return value;
    }

}
