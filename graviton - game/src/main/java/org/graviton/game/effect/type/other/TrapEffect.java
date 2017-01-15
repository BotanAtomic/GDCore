package org.graviton.game.effect.type.other;

import org.graviton.game.effect.Effect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.trap.Glyph;
import org.graviton.game.trap.Trap;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Botan on 30/12/2016. 16:35
 */
public class TrapEffect implements Effect {
    private final boolean glyph;

    public TrapEffect(boolean glyph) {
        this.glyph = glyph;
    }

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        if (!glyph)
            new Trap(fighter, effect.getSpell().getTemplate(), effect, fighter.getCreature().entityFactory().getSpellTemplate(effect.getFirst()).getLevel((byte) effect.getSecond()),
                    new ArrayList<>(effect.getZone().getCells(selectedCell, fighter)));
        else
            new Glyph(fighter, effect.getSpell().getTemplate(), effect, fighter.getCreature().entityFactory().getSpellTemplate(effect.getFirst()).getLevel((byte) effect.getSecond()),
                    new ArrayList<>(effect.getZone().getCells(selectedCell, fighter)));
    }

    @Override
    public Effect copy() {
        return new TrapEffect(this.glyph);
    }
}
