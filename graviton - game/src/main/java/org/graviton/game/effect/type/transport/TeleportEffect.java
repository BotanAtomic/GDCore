package org.graviton.game.effect.type.transport;

import org.graviton.game.effect.Effect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.trap.AbstractTrap;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.Collection;


/**
 * Created by Botan on 27/12/2016. 03:17
 */
public class TeleportEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        if (selectedCell.isWalkable() && selectedCell.getCreatures().isEmpty()) {
            fighter.setFightCell(selectedCell);
            fighter.getFight().send(FightPacketFormatter.actionMessage(FightAction.TELEPORT_EVENT, fighter.getId(), fighter.getId(), selectedCell.getId()));

            Collection<AbstractTrap> traps = fighter.getFight().getTrap(selectedCell.getId());

            if (traps != null)
                traps.forEach(trap -> trap.onTrapped(fighter));
        }
    }

    @Override
    public Effect copy() {
        return new TeleportEffect();
    }
}
