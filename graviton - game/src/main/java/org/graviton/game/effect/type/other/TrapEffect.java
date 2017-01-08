package org.graviton.game.effect.type.other;

import org.graviton.game.effect.Effect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.trap.Trap;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Botan on 30/12/2016. 16:35
 */
public class TrapEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        new Trap(fighter, effect.getSpell(), effect, fighter.getCreature().entityFactory().getSpellTemplate(effect.getFirst()).getLevel((byte) effect.getSecond()),
                new ArrayList<>(effect.getZone().getCells(selectedCell, fighter)));
    }
}
