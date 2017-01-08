package org.graviton.game.effect.type.buff;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.type.SimpleStatisticBuff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.utils.Utils;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.graviton.network.game.protocol.FightPacketFormatter.actionMessage;

/**
 * Created by Botan on 29/12/2016. 15:37
 */
public class StatisticDodgeEffect implements Effect {
    private final CharacteristicType characteristicType;

    public StatisticDodgeEffect(CharacteristicType characteristicType) {
        this.characteristicType = characteristicType;
    }

    public static byte value(Fighter fighter, Fighter target, short value, CharacteristicType characteristicType) {
        short fighterDodge = fighter.getStatistics().get(pointToDodge(characteristicType)).total();
        short targetDodge = target.getStatistics().get(pointToDodge(characteristicType)).total();

        short actionPoint = target.getStatistics().get(characteristicType).total();

        AtomicInteger toRemove = new AtomicInteger(0);

        IntStream.range(0, value).forEach(i -> {
            short point = (short) (target.getCurrentPoint(characteristicType) - toRemove.get());
            double dodgeFactor = (fighterDodge == 0 ? 1 : fighterDodge) / (targetDodge == 0 ? 1 : targetDodge);
            float factor = ((float) point / (float) actionPoint);
            double percent = StrictMath.ceil((dodgeFactor * factor * 50));

            System.err.println(percent);

            if (Utils.random(1, 100) < percent && percent > 0)
                toRemove.incrementAndGet();
        });

        return (byte) (toRemove.get() * -1);
    }

    private static CharacteristicType pointToDodge(CharacteristicType characteristicType) {
        switch (characteristicType) {
            case ActionPoints:
                return CharacteristicType.DodgeActionPoints;
            case MovementPoints:
                return CharacteristicType.DodgeMovementPoints;
            default:
                return null;
        }
    }

    static FightAction fightAction(CharacteristicType characteristicType) {
        switch (characteristicType) {
            case ActionPoints:
                return FightAction.DODGE_ACTION_POINT;
            case MovementPoints:
                return FightAction.DODGE_MOVEMENT_POINT;
            default:
                return null;
        }
    }

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            byte value = value(fighter, target, effect.getSecond() < 0 ? effect.getFirst() : effect.getDice().random(), characteristicType);

            if (value == 0) {
                fighter.getFight().send(actionMessage(fightAction(characteristicType), fighter.getId(), target.getId(), effect.getFirst()));
                return;
            }

            if (effect.getFirst() + value > 0)
                fighter.getFight().send(actionMessage(fightAction(characteristicType), fighter.getId(), target.getId(), effect.getFirst() + value));

            fighter.getFight().send(actionMessage((short) effect.getType().value(), fighter.getId(), target.getId(), value, effect.getTurns()));

            new SimpleStatisticBuff(characteristicType, true, target, value, effect, effect.getTurns());
        });
    }
}
