package org.graviton.game.effect.type.transport;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.state.State;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.trap.AbstractTrap;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.Collection;

/**
 * Created by Botan on 14/01/2017. 12:58
 */
public class LaunchEffect implements Effect {
    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        if (fighter.getHolding() != null) {
            Fighter target = fighter.getHolding();

            fighter.getFight().send(FightPacketFormatter.actionMessage((short) effect.getType().value(), fighter.getId(), selectedCell.getId()));

            target.setFightCell(selectedCell);
            removeState(target, State.Carried);

            removeState(fighter, State.Carrier);
            target.setHoldingBy(null);
            fighter.setHolding(null);

            Collection<AbstractTrap> traps = fighter.getFight().getTrap(selectedCell.getId());

            if (traps != null)
                traps.forEach(trap -> trap.onTrapped(target));
        }
    }

    private void removeState(Fighter fighter, State state) {
        fighter.getBuffManager().removeState(state);
        fighter.getFight().send(FightPacketFormatter.actionMessage(FightAction.STATE_EVENT, fighter.getId(), fighter.getId(), state.getValue(), 0));
    }

    @Override
    public Effect copy() {
        return new LaunchEffect();
    }
}
