package org.graviton.game.effect.type.damage;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.enums.DamageType;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;

import java.util.Collection;

/**
 * Created by Botan on 28/12/2016. 18:07
 */
public class PunishmentLifeEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            double radian = ((double) 2 * Math.PI * (((double) fighter.getLife().getCurrent() / (double) fighter.getLife().getMaximum()) - 0.5));
            int damage = (int) (((Math.pow((Math.cos(radian) + 1), 2)) / (double) 4) * (((double) effect.getFirst() / (double) 100) * fighter.getLife().getMaximum()));

            damage = DamageEffect.reduce(damage, fighter, target, DamageType.NEUTRAL);

            fighter.getFight().hit(fighter, target, damage);
        });
    }

}
