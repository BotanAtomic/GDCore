package org.graviton.game.effect.type.damage;

import org.graviton.game.client.player.Player;
import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.type.PoisonBuff;
import org.graviton.game.effect.buff.type.PunishmentBuff;
import org.graviton.game.effect.buff.type.RandomAttackResultBuff;
import org.graviton.game.effect.enums.DamageType;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
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

        AtomicInteger damage = new AtomicInteger((effect.getDice().random() + boost) * ((100 + statistics.get(DAMAGE_TO_CHARACTERISTIC.apply(damageType)).total() +
                statistics.get(CharacteristicType.DamagePer).total() + (100 * statistics.get(CharacteristicType.MultiplyDamage).total())) / 100) + statistics.get(CharacteristicType.Damage).total());

        damage.set(reduce(damage.get(), fighter, target, damageType));


        return damage.get();
    }

    static int reduce(int baseDamage, Fighter fighter, Fighter target, DamageType damageType) {
        Statistics targetStatistics = target.getStatistics();

        AtomicInteger damage = new AtomicInteger(baseDamage);

        damage.addAndGet(-applyReturnDamage(fighter, target, targetStatistics));
        damage.addAndGet(getDamageReduction(damageType, targetStatistics, baseDamage));

        damage.addAndGet(-getReduction(targetStatistics, damageType));

        short armor = target.getStatistics().get(CharacteristicType.Armor).total();

        if (armor != 0) {
            target.getFight().send(FightPacketFormatter.actionMessage(FightAction.ARMOR, target.getId(), target.getId(), armor));
            damage.addAndGet(-armor);
        }

        damage.set(Utils.limit(damage.get(), target.getLife().getCurrent()));

        if (target.getDamageSuffer() != 0) {
            double factor = (double) target.getDamageSuffer() / 100;
            damage.addAndGet((int) (damage.get() * factor));
        }

        target.getBuffs(PunishmentBuff.class).forEach(buff -> ((PunishmentBuff) buff).add((short) (damage.get() / (fighter.getCreature() instanceof Player ? 2 : 1))));

        RandomAttackResultBuff randomAttackResultBuff;
        if ((randomAttackResultBuff = (RandomAttackResultBuff) target.getBuff(RandomAttackResultBuff.class)) != null) {
            if (new Random().nextBoolean())
                damage.set(damage.get() * randomAttackResultBuff.getRateDamage());
            else
                damage.set(damage.get() * randomAttackResultBuff.getRateHeal() * -1);
        }

        return Utils.limit(damage.get(), damage.get());
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

    private static int getReduction(Statistics targetStatistics, DamageType damageType) {
        if (damageType == DamageType.NEUTRAL || damageType == DamageType.EARTH)
            return targetStatistics.get(CharacteristicType.ReducePhysic).total();
        else
            return targetStatistics.get(CharacteristicType.ReduceMagic).total();
    }

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            if (effect.getTurns() < 1)
                fighter.getFight().hit(fighter, target, damage(effect, fighter, target, this.damageType, fighter.getSpellBoost(effect.getSpell().getId())));
            else
                new PoisonBuff(target, this.damageType, effect, effect.getTurns());
        });
    }
}
