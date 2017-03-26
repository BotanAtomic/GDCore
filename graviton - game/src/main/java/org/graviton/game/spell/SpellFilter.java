package org.graviton.game.spell;

import org.graviton.game.fight.Fighter;
import org.graviton.game.spell.Spell;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Botan on 03/03/2017. 21:10
 */
public class SpellFilter {
    private final Fighter fighter;

    private List<Spell> buffs, glyph, invocation, attack, best;

    public SpellFilter(Fighter fighter) {
        this.fighter = fighter;
    }

    public List<Spell> getGlyph() {
        if(this.glyph != null)
            return this.glyph;
        return (this.glyph = fighter.getSpells().stream().filter(Spell::isGlyphEffect).collect(Collectors.toList()));
    }

    public List<Spell> getBuffs() {
        if(this.buffs != null)
            return this.buffs;
        return (this.buffs = fighter.getSpells().stream().filter(Spell::isBuffEffect).collect(Collectors.toList()));
    }

    public List<Spell> getInvocation() {
        if(this.invocation != null)
            return this.invocation;
        return (this.invocation = fighter.getSpells().stream().filter(Spell::isInvocationEffect).collect(Collectors.toList()));
    }

    public List<Spell> getBest() {
        if(this.best != null)
            return this.best;
        return (this.best = fighter.getSpells().stream().filter(spell -> spell.getTemplate().getType() == 0 && spell.getMaximumRange() < 3).collect(Collectors.toList()));
    }

    public List<Spell> getAttack() {
        if(this.attack != null)
            return this.attack;
        return (this.attack = fighter.getSpells().stream().filter(spell -> spell.getTemplate().getType() == 0 && spell.getMaximumRange() > 1).collect(Collectors.toList()));

    }

}
