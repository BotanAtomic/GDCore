package org.graviton.game.effect.type.damage;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.enums.DamageType;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.utils.Utils;

import java.util.Collection;

/**
 * Created by Botan on 28/12/2016. 01:16
 */
public class StealEffect implements Effect {
    private final DamageType damageType;

    private boolean onlyAlly;

    public StealEffect(DamageType damageType, boolean onlyAlly) {
        this.damageType = damageType;
        this.onlyAlly = onlyAlly;
    }

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            if (onlyAlly && !target.getTeam().equals(fighter.getTeam()))
                return;

            int damage = DamageEffect.damage(effect, fighter, target, damageType, 0);
            int heal = damage / 2;
            fighter.getFight().hit(fighter, target, damage);
            fighter.getFight().hit(target, fighter, Utils.limit(heal, fighter.getLife().getMaximum() - fighter.getLife().getCurrent()) * -1);
        });
    }
}
