package org.graviton.game.effect.type.buff;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.type.SimpleStatisticBuff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.statistics.common.CharacteristicType;

import java.util.Collection;

import static org.graviton.network.game.protocol.FightPacketFormatter.actionMessage;

/**
 * Created by Botan on 01/01/2017. 21:31
 */
public class RemoveDamageEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            fighter.getFight().send(actionMessage((short) effect.getType().value(), fighter.getId(), target.getId(), effect.getFirst(), effect.getTurns()));
            new SimpleStatisticBuff(CharacteristicType.Damage, false, target, effect.getFirst() * -1, effect, effect.getTurns());
        });
    }
}
