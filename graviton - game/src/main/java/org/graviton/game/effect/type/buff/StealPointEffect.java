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
 * Created by Botan on 29/12/2016. 23:02
 */
public class StealPointEffect implements Effect {
    private CharacteristicType characteristicType;

    public StealPointEffect(CharacteristicType characteristicType) {
        this.characteristicType = characteristicType;
    }

    private int addEffect() {
        switch (characteristicType) {
            case ActionPoints:
                return SpellEffects.AddAP.value();
            case MovementPoints:
                return SpellEffects.AddMP.value();
            default:
                return 0;
        }
    }

    private int removeEffect() {
        switch (characteristicType) {
            case ActionPoints:
                return SpellEffects.SubAP.value();
            case MovementPoints:
                return SpellEffects.SubMP.value();
            default:
                return 0;
        }
    }

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        short turn = effect.getTurns() == 0 ? 1 : effect.getTurns();

        targets.forEach(target -> {
            int value = StatisticDodgeEffect.value(fighter, target, effect.getFirst(), characteristicType);
            byte dodge = (byte) (effect.getFirst() + value);

            if (value == 0) {
                fighter.getFight().send(actionMessage(StatisticDodgeEffect.fightAction(characteristicType), fighter.getId(), target.getId(), effect.getFirst()));
                return;
            }

            if (dodge > 0)
                fighter.getFight().send(actionMessage(StatisticDodgeEffect.fightAction(characteristicType), fighter.getId(), target.getId(), dodge));

            fighter.getFight().send(actionMessage((short) removeEffect(), fighter.getId(), target.getId(), value, turn));
            fighter.getFight().send(actionMessage((short) addEffect(), fighter.getId(), fighter.getId(), value * -1, turn));

            new SimpleStatisticBuff(characteristicType, true, fighter, value * -1, effect, turn);
            new SimpleStatisticBuff(characteristicType, false, target, value, effect, turn);
        });
    }
}
