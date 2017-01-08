package org.graviton.game.effect.type.buff;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.type.SimpleStatisticBuff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.statistics.common.CharacteristicType;

import java.util.Collection;

import static org.graviton.network.game.protocol.FightPacketFormatter.actionMessage;


/**
 * Created by Botan on 30/12/2016. 20:50
 */
public class StealRangeEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            fighter.getFight().send(actionMessage(FightAction.ADD_RANGE, fighter.getId(), fighter.getId(), effect.getFirst(), effect.getTurns()));
            fighter.getFight().send(actionMessage(FightAction.REMOVE_RANGE, fighter.getId(), target.getId(), effect.getFirst(), effect.getTurns()));

            new SimpleStatisticBuff(CharacteristicType.RangePoints, false, target, effect.getFirst() * -1, effect, effect.getTurns());
            new SimpleStatisticBuff(CharacteristicType.RangePoints, true, fighter, effect.getFirst(), effect, effect.getTurns());
        });
    }
}
