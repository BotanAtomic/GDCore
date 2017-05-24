package org.graviton.game.effect.type.other;

import org.graviton.game.effect.Effect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.Collection;

/**
 * Created by Botan on 09/01/2017. 21:09
 */
public class ClearBuffEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            target.getBuffManager().clearBuffs();
            target.getFight().send(FightPacketFormatter.actionMessage((short) effect.getType().value(), fighter.getId(), target.getId()));
        });
    }

    @Override
    public Effect copy() {
        return new ClearBuffEffect();
    }
}
