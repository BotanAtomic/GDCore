package org.graviton.game.effect.type.damage;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.type.HealBuff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.statistics.common.CharacteristicType;

import java.util.Collection;

/**
 * Created by Botan on 28/12/2016. 18:28
 */
public class HealEffect implements Effect {

    public static void heal(Fighter fighter, Fighter target, SpellEffect effect, int boost) {
        double factor = (100 + fighter.getStatistics().get(CharacteristicType.Intelligence).total()) / 100;
        int heal = (int) ((effect.getDice().random() + boost) * factor + fighter.getStatistics().get(CharacteristicType.HealPoints).total());

        if (target.getLife().getCurrent() + heal > target.getLife().getMaximum())
            heal = target.getLife().getMaximum() - target.getLife().getCurrent();

        target.getFight().hit(fighter, target, heal * -1);
    }

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            if (effect.getTurns() <= 0)
                heal(fighter, target, effect, effect.getThird());
            else
                new HealBuff(target, effect);
        });
    }

    @Override
    public Effect copy() {
        return new HealEffect();
    }
}
