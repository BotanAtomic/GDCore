package org.graviton.game.effect.type.buff;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.type.SimpleStatisticBuff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.spell.common.SpellEffects;
import org.graviton.game.statistics.common.CharacteristicType;

import java.util.Collection;

import static org.graviton.network.game.protocol.FightPacketFormatter.actionMessage;

/**
 * Created by Botan on 02/01/2017. 15:58
 */
public class StealStatisticEffect implements Effect {
    private final CharacteristicType characteristicType;

    public StealStatisticEffect(CharacteristicType characteristicType) {
        this.characteristicType = characteristicType;
    }

    private int addEffect() {
        switch (characteristicType) {
            case Agility:
                return SpellEffects.AddAgility.value();
            case Intelligence:
                return SpellEffects.AddIntelligence.value();
            case Strength:
                return SpellEffects.AddStrength.value();
            case Chance:
                return SpellEffects.AddChance.value();
            default:
                return 0;
        }
    }

    private int removeEffect() {
        switch (characteristicType) {
            case Agility:
                return SpellEffects.SubAgility.value();
            case Intelligence:
                return SpellEffects.SubIntelligence.value();
            case Strength:
                return SpellEffects.SubStrength.value();
            case Chance:
                return SpellEffects.SubChance.value();
            default:
                return 0;
        }
    }

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            fighter.getFight().send(actionMessage((short) removeEffect(), target.getId(), target.getId(), effect.getFirst(), effect.getTurns()));
            fighter.getFight().send(actionMessage((short) addEffect(), fighter.getId(), fighter.getId(), effect.getFirst(), effect.getTurns()));

            new SimpleStatisticBuff(characteristicType, false, target, effect.getFirst() * -1, effect, effect.getTurns());
            new SimpleStatisticBuff(characteristicType, true, fighter, effect.getFirst(), effect, effect.getTurns());
        });
    }

    @Override
    public Effect copy() {
        return new StealStatisticEffect(this.characteristicType);
    }
}
