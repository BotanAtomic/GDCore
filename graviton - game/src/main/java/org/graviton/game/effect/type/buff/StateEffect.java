package org.graviton.game.effect.type.buff;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.type.StateBuff;
import org.graviton.game.effect.state.State;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.Collection;

/**
 * Created by Botan on 02/01/2017. 00:21
 */
public class StateEffect implements Effect {
    private final boolean remove;

    public StateEffect(boolean remove) {
        this.remove = remove;
    }

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            fighter.getFight().send(FightPacketFormatter.actionMessage(FightAction.STATE_EVENT, fighter.getId(), target.getId(), effect.getThird(), remove ? 0 : 1));

            if (remove) target.getBuffManager().removeState(State.get((byte) effect.getThird()));
            else new StateBuff(target, effect, effect.getTurns());
        });
    }

    @Override
    public Effect copy() {
        return new StateEffect(this.remove);
    }
}
