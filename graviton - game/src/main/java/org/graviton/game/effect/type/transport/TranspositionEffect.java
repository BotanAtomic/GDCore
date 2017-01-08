package org.graviton.game.effect.type.transport;

import org.graviton.game.effect.Effect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.Collection;

import static java.lang.String.valueOf;

/**
 * Created by Botan on 28/12/2016. 16:44
 */
public class TranspositionEffect implements Effect {
    private final boolean ally;
    private final boolean enemy;

    public TranspositionEffect(boolean ally, boolean enemy) {
        this.ally = ally;
        this.enemy = enemy;
    }

    public static void transpose(Fighter fighter, Fighter target) {
        Cell cell = fighter.getFightCell();
        Cell targetCell = target.getFightCell();

        cell.getCreatures().clear();
        targetCell.getCreatures().clear();

        fighter.setFightCell(targetCell);
        target.setFightCell(cell);

        fighter.getFight().send(FightPacketFormatter.actionMessage(FightAction.TELEPORT_EVENT, target.getId(), valueOf(target.getId()), valueOf(cell.getId())));
        fighter.getFight().send(FightPacketFormatter.actionMessage(FightAction.TELEPORT_EVENT, fighter.getId(), valueOf(fighter.getId()), valueOf(targetCell.getId())));

    }

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            if (ally && enemy)
                transpose(fighter, target);
            else if (ally && target.getTeam().getSide().ordinal() == fighter.getTeam().getSide().ordinal())
                transpose(fighter, target);
            else if (!ally && target.getTeam().getSide().ordinal() != fighter.getTeam().getSide().ordinal())
                transpose(fighter, target);
        });
    }
}
