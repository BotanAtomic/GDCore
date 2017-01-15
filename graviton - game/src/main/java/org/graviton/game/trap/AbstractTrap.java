package org.graviton.game.trap;

import lombok.Data;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.Spell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.spell.SpellTemplate;

import java.util.List;

/**
 * Created by Botan on 08/01/2017. 19:25
 */

@Data
public abstract class AbstractTrap {
    protected final Fighter owner;
    protected final Fight fight;
    protected final SpellTemplate originalSpell;
    protected final SpellEffect spellEffect;
    protected final Spell spell;
    protected final Cell center;
    protected final List<Cell> cells;

    protected final int color;

    public AbstractTrap(Fighter owner, SpellTemplate originalSpell, int color, SpellEffect spellEffect, Spell spell, List<Cell> cells) {
        this.owner = owner;
        this.fight = owner.getFight();
        this.originalSpell = originalSpell;
        this.spellEffect = spellEffect;
        this.spell = spell;
        this.center = cells.get(0);
        this.cells = cells;

        this.color = color;

        show();
    }


    public abstract void onTrapped(Fighter fighter);


    protected abstract void applyEffect(Fighter fighter);

    public boolean containCell(short cellId) {
        return cells.stream().filter(cell -> cell.getId() == cellId).count() != 0;
    }


    abstract void show();

    abstract void hide();

    public abstract void check(Fighter fighter);

}
