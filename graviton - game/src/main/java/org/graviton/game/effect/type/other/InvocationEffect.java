package org.graviton.game.effect.type.other;

import org.graviton.game.creature.monster.Monster;
import org.graviton.game.creature.monster.extra.Double;
import org.graviton.game.effect.Effect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.fight.turn.FightTurn;
import org.graviton.game.fight.turn.FightTurnList;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.position.Location;
import org.graviton.game.spell.SpellEffect;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.Collection;

import static org.graviton.network.game.protocol.FightPacketFormatter.showFighter;
import static org.graviton.network.game.protocol.FightPacketFormatter.turnListMessage;

/**
 * Created by Botan on 15/01/2017. 21:55
 */
public class InvocationEffect implements Effect {
    private final boolean isStatic;

    public static void invokeDouble(Fighter master, Double clone, Cell selectedCell) {
        clone.setFight(master.getFight());

        master.getTeam().addFighter(clone);

        clone.setMaster(master);

        clone.setLocation(new Location(master.getLocation().getMap(), selectedCell.getId(), master.getLocation().getOrientation()));
        clone.setFightCell(selectedCell);
        clone.initializeFighterPoints();
        master.getFight().send(FightPacketFormatter.actionMessage(FightAction.INVOCATION, master.getId(), showFighter(clone).substring(3)));

        FightTurnList turnList = master.getFight().getTurnList();
        turnList.getTurns().add(turnList.getTurns().indexOf(master.getTurn()) + 1, new FightTurn(master.getFight(), clone));
        master.getFight().send(FightPacketFormatter.actionMessage((short) 999, master.getId(), turnListMessage(turnList.getTurns())));

        master.getInvocations().add(clone);

        master.getFight().getTrap(selectedCell.getId()).forEach(trap -> trap.onTrapped(clone));
    }

    public InvocationEffect(boolean isStatic) {
        this.isStatic = isStatic;
    }

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        Monster monster = fighter.getCreature().entityFactory().getMonsterTemplate(effect.getFirst()).getByLevel(effect.getSecond()).copy();

        if (monster != null) {
            monster.applyFighterStatistics(fighter);
            monster.setFight(fighter.getFight());
            monster.setId(fighter.getFight().nextId());
            fighter.getTeam().addFighter(monster);
            monster.setMaster(fighter);
            monster.setLocation(new Location(fighter.getLocation().getMap(), selectedCell.getId(), fighter.getLocation().getOrientation()));
            monster.setFightCell(selectedCell);
            monster.initializeFighterPoints();
            monster.setStatic(isStatic);

            fighter.getFight().send(FightPacketFormatter.actionMessage(FightAction.INVOCATION, fighter.getId(), showFighter(monster).substring(3)));

            if (!isStatic) {
                FightTurnList turnList = fighter.getFight().getTurnList();
                turnList.getTurns().add(turnList.getTurns().indexOf(fighter.getTurn()) + 1, new FightTurn(fighter.getFight(), monster));
                fighter.getFight().send(FightPacketFormatter.actionMessage((short) 999, fighter.getId(), turnListMessage(turnList.getTurns())));
            }

            fighter.getInvocations().add(monster);

            fighter.getFight().getTrap(selectedCell.getId()).forEach(trap -> trap.onTrapped(monster));
        }

    }

    @Override
    public Effect copy() {
        return new InvocationEffect(isStatic);
    }
}
