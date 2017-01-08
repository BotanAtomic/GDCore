package org.graviton.game.trap;

import lombok.Data;
import org.graviton.game.effect.type.push.PushBackEffect;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.Spell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.spell.SpellTemplate;
import org.graviton.game.spell.common.SpellEffects;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.List;

/**
 * Created by Botan on 30/12/2016. 16:18
 */

@Data
public class Trap {
    private final Fighter owner;
    private final Fight fight;
    private final SpellTemplate originalSpell;
    private final SpellEffect spellEffect;
    private final Spell spell;
    private final Cell center;
    private final List<Cell> cells;

    public Trap(Fighter owner, SpellTemplate originalSpell, SpellEffect spellEffect, Spell spell, List<Cell> cells) {
        this.owner = owner;
        this.fight = owner.getFight();
        this.originalSpell = originalSpell;
        this.spellEffect = spellEffect;
        this.spell = spell;
        this.center = cells.get(0);
        this.cells = cells;

        show();
    }

    public boolean containCell(short cellId) {
        return cells.stream().filter(cell -> cell.getId() == cellId).count() != 0;
    }

    public void show() {
        send(FightPacketFormatter.trapAddedMessage(owner.getId(), center.getId(), spellEffect.getZone().getLength()));
        fight.getTraps().put(center.getId(), this);
    }

    private void hide() {
        send(FightPacketFormatter.trapDeletedMessage(owner.getId(), center.getId(), spellEffect.getZone().getLength()));
        fight.getTraps().remove(center.getId());
    }

    public void onTrapped(Fighter fighter) {
        if (!containCell(fighter.getFightCell().getId()))
            this.cells.add(fighter.getFightCell());

        fight.send(FightPacketFormatter.trapUsedMessage(fighter.getId(), originalSpell.getId(), center.getId(), owner.getId()));

        hide();

        spell.getEffects().forEach(effect -> {
            if (effect.getType() == SpellEffects.PushBack)
                new PushBackEffect().applyForTrap(this, fighter.getFight(), cells, effect);
            else
                effect.getType().apply(owner, spellEffect.getZone().getTargets(this.cells, fighter), center, effect);
        });

    }

    private void send(String data) {
        owner.getTeam().send(data);
    }


}
