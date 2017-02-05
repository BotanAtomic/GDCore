package org.graviton.game.effect.type.other;

import org.graviton.game.client.player.Player;
import org.graviton.game.creature.monster.Monster;
import org.graviton.game.effect.Effect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.fight.turn.FightTurn;
import org.graviton.game.fight.turn.FightTurnList;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

import java.util.ArrayList;
import java.util.Collection;

import static org.graviton.network.game.protocol.FightPacketFormatter.showFighter;

/**
 * Created by Botan on 21/01/2017. 12:17
 */
public class ResuscitateFighterEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        Fighter target = fighter.getTeam().getLastDead();

        if (target != null) {
            fighter.getFight().send(FightPacketFormatter.turnListMessage(new ArrayList<>()));

            target.setDead(false);
            target.getLife().setPercent(effect.getFirst());
            target.setMaster(fighter);
            target.setFightCell(selectedCell);
            fighter.getFight().send(FightPacketFormatter.actionMessage(FightAction.INVOCATION, fighter.getId(), showFighter(target).substring(3)));

            FightTurnList turnList = fighter.getFight().getTurnList();

            if (target instanceof Monster)
                turnList.getTurns().add(turnList.getTurns().indexOf(fighter.getTurn()) + 1, new FightTurn(fighter.getFight(), target));
            else if (target instanceof Player)
                target.send(PlayerPacketFormatter.asMessage((Player) target));

            fighter.getFight().send(FightPacketFormatter.turnListMessage(turnList.getTurns()));
            fighter.getInvocations().add(target);
            fighter.getFight().getTrap(selectedCell.getId()).forEach(trap -> trap.onTrapped(target));
        }
    }

    @Override
    public Effect copy() {
        return new ResuscitateFighterEffect();
    }
}
