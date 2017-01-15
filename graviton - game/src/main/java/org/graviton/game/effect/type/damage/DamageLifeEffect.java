package org.graviton.game.effect.type.damage;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.enums.DamageType;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;

import java.util.Collection;

/**
 * Created by Botan on 07/01/2017. 21:50
 */
public class DamageLifeEffect implements Effect {
    private final DamageType damageType;

    public DamageLifeEffect(DamageType damageType) {
        this.damageType = damageType;
    }

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            double percent = target.getLife().getCurrent() / 100;
            double damage = percent * (effect.getSecond() != -1 ? effect.getDice().random() : effect.getFirst());

            if (target.canReturnSpell(effect.getSpell())) {
                fighter.getFight().hit(target, fighter, DamageEffect.reduce((int) damage, fighter, target, damageType));
                return;
            }

            fighter.getFight().hit(fighter, target, DamageEffect.reduce((int) damage, fighter, target, damageType));
        });
    }

    @Override
    public Effect copy() {
        return new DamageLifeEffect(this.damageType);
    }
}
