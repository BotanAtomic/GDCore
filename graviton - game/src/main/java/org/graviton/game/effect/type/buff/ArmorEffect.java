package org.graviton.game.effect.type.buff;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.type.SimpleStatisticBuff;
import org.graviton.game.effect.enums.DamageType;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.statistics.common.CharacteristicType;

import java.util.Collection;

/**
 * Created by Botan on 08/01/2017. 17:42
 */
public class ArmorEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> new SimpleStatisticBuff(convert(DamageType.get(effect.getSecond())), true, target, effect.getFirst(), effect, effect.getTurns()));
    }

    @Override
    public Effect copy() {
        return new ArmorEffect();
    }

    private CharacteristicType convert(DamageType damageType) {
        switch (damageType) {
            case NEUTRAL:
                return CharacteristicType.ReducePhysic;
            case FIRE:
                return CharacteristicType.ArmorFire;
            case WATER:
                return CharacteristicType.ArmorWater;
            case WIND:
                return CharacteristicType.ArmorWind;
        }
        return null;
    }
}
