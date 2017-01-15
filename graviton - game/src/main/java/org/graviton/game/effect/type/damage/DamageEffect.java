package org.graviton.game.effect.type.damage;

import javafx.util.Pair;
import org.graviton.game.client.player.Player;
import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.type.PoisonBuff;
import org.graviton.game.effect.buff.type.PunishmentBuff;
import org.graviton.game.effect.buff.type.RandomAttackResultBuff;
import org.graviton.game.effect.enums.DamageType;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.items.common.Bonus;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.common.Statistics;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.utils.Utils;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.graviton.converter.Converters.*;


/**
 * Created by Botan on 26/12/2016. 00:50
 */
public class DamageEffect implements Effect {
    private final DamageType damageType;

    public DamageEffect(DamageType damageType) {
        this.damageType = damageType;
    }


    public static int damage(SpellEffect effect, Fighter fighter, Fighter target, DamageType damageType, int boost) {
        if (!fighter.isVisible())
            fighter.setVisibleAfterAttack();

        Statistics statistics = fighter.getStatistics();

        AtomicInteger damage = new AtomicInteger((effect.getDice().random() + boost) *
                ((100 + statistics.get(DAMAGE_TO_CHARACTERISTIC.apply(damageType)).total() +
                        statistics.get(CharacteristicType.DamagePer).total() + (100 * statistics.get(CharacteristicType.MultiplyDamage).total())) / 100) +
                statistics.get(CharacteristicType.Damage).total() + specialDamage(statistics, damageType));

        damage.set(reduce(damage.get(), fighter, target, damageType));

        return damage.get();
    }

    private static int specialDamage(Statistics statistics, DamageType damageType) {
        if (damageType == DamageType.NEUTRAL || damageType == DamageType.EARTH)
            return statistics.get(CharacteristicType.DamagePhysic).total();
        else
            return statistics.get(CharacteristicType.DamageMagic).total();
    }

    static int reduce(int baseDamage, Fighter fighter, Fighter target, DamageType damageType) {
        Statistics targetStatistics = target.getStatistics();

        AtomicInteger damage = new AtomicInteger(baseDamage);

        damage.addAndGet(-applyReturnDamage(fighter, target, targetStatistics));
        damage.addAndGet(getDamageReduction(damageType, targetStatistics, baseDamage));

        Pair<Short, Short> reduction = getReduction(targetStatistics, damageType);
        short armor = (short) (target.getStatistics().get(CharacteristicType.Armor).total() + reduction.getKey());

        if (armor != 0) {
            target.getFight().send(FightPacketFormatter.actionMessage(FightAction.ARMOR, target.getId(), target.getId(), armor - reduction.getValue()));
            damage.addAndGet(-armor);
        }

        if (target.getDamageSuffer() != 0) {
            double factor = (double) target.getDamageSuffer() / 100;
            damage.addAndGet((int) (damage.get() * factor));
        }

        damage.set(Utils.limit(damage.get(), target.getLife().getCurrent()));

        target.getBuffs(PunishmentBuff.class).forEach(buff -> ((PunishmentBuff) buff).add((short) (damage.get() / (fighter.getCreature() instanceof Player ? 2 : 1))));

        RandomAttackResultBuff randomAttackResultBuff;
        if ((randomAttackResultBuff = (RandomAttackResultBuff) target.getBuff(RandomAttackResultBuff.class)) != null) {
            if (new Random().nextBoolean())
                damage.set(damage.get() * randomAttackResultBuff.getRateDamage());
            else
                damage.set(damage.get() * randomAttackResultBuff.getRateHeal() * -1);
        }

        return damage.get();
    }

    private static int applyReturnDamage(Fighter fighter, Fighter target, Statistics statistics) {
        int itemReturn;
        if ((itemReturn = statistics.get(CharacteristicType.DamageReturn).total()) > 0) {
            int total = ((int) ((1 + ((double) statistics.get(CharacteristicType.Wisdom).total() / (double) 100)) + itemReturn));
            target.getFight().send(FightPacketFormatter.actionMessage(FightAction.RETURN_DAMAGE, -1, target.getId(), total));
            target.getFight().hit(target, fighter, total);
            return total;
        } else
            return 0;
    }

    private static int getDamageReduction(DamageType damageType, Statistics targetStatistics, int damage) {
        if (damageType == DamageType.NULL)
            return 0;

        short resistancePercent = targetStatistics.get(DAMAGE_TO_RESISTANCE_P.apply(damageType)).total();
        short resistance = targetStatistics.get(DAMAGE_TO_RESISTANCE.apply(damageType)).total();

        return ((int) (((double) (1 - resistancePercent) / (double) 100) * (damage - resistance)));
    }

    private static Pair<Short, Short> getReduction(Statistics targetStatistics, DamageType damageType) {
        if (damageType == DamageType.NEUTRAL || damageType == DamageType.EARTH)
            return new Pair<>(targetStatistics.get(CharacteristicType.ReducePhysic).total(), targetStatistics.get(CharacteristicType.ReducePhysic).equipment());
        else
            return new Pair<>(totalMagic(targetStatistics, damageType), targetStatistics.get(CharacteristicType.ReduceMagic).equipment());
    }

    private static short totalMagic(Statistics statistics, DamageType damageType) {
        switch (damageType) {
            case NEUTRAL:
                return statistics.get(CharacteristicType.ReducePhysic).total();
            case FIRE:
                return statistics.get(CharacteristicType.ArmorFire).total();
            case WATER:
                return statistics.get(CharacteristicType.ArmorWater).total();
            case WIND:
                return statistics.get(CharacteristicType.ArmorWind).total();
        }
        return 0;
    }

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            if (effect.getTurns() < 1) {

                if (effect.getSpell() != null && target.canReturnSpell(effect.getSpell())) {
                    fighter.getFight().hit(target, fighter, damage(effect, fighter, target, this.damageType, fighter.getSpellBoost(effect.getSpellId())));
                    return;
                }
                fighter.getFight().hit(fighter, target, damage(effect, fighter, target, this.damageType, fighter.getSpellBoost(effect.getSpellId()) + effect.getThird()));
            } else
                new PoisonBuff(target, this.damageType, effect, effect.getTurns());
        });
    }

    public void applyWeapon(Fighter fighter, Fighter target) {
        if (target != null)
            fighter.getFight().hit(fighter, target, damage(new SpellEffect(-1) {{
                setDice(new Bonus((short) 1, (short) 5, (short) 0));
            }}, fighter, target, this.damageType, 0));
    }

    @Override
    public Effect copy() {
        return new DamageEffect(this.damageType);
    }
}
