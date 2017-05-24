package org.graviton.game.effect.type.transport;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.state.State;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.Collection;

/**
 * Created by Botan on 13/01/2017. 23:26
 */
public class HoldingEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            if (target.isStatic())
                return;

            target.setFightCell(fighter.getFightCell());

            target.setHoldingBy(fighter);
            addState(target, State.Carried);

            fighter.setHolding(target);
            addState(fighter, State.Carrier);

            fighter.getFight().send(FightPacketFormatter.actionMessage((short) effect.getType().value(), fighter.getId(), target.getId()));

        });
    }

    private void addState(Fighter fighter, State state) {
        fighter.getBuffManager().addState(state);
        fighter.getFight().send(FightPacketFormatter.actionMessage(FightAction.STATE_EVENT, fighter.getId(), fighter.getId(), state.getValue(), 1));
    }

    @Override
    public Effect copy() {
        return new HoldingEffect();
    }
}
