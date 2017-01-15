package org.graviton.game.trap;

import org.graviton.game.effect.type.push.PushBackEffect;
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

public class Trap extends AbstractTrap {

    public Trap(Fighter owner, SpellTemplate originalSpell, SpellEffect spellEffect, Spell spell, List<Cell> cells) {
        super(owner, originalSpell, 7, spellEffect, spell, cells);
    }

    @Override
    public void show() {
        send(FightPacketFormatter.trapAddedMessage(owner.getId(), center.getId(), spellEffect.getZone().getLength(), 7));
        send(FightPacketFormatter.localTrapAddedMessage(owner.getId(), center.getId()));
        fight.getTraps().put(center.getId(), this);
    }

    @Override
    public void hide() {
        send(FightPacketFormatter.trapDeletedMessage(owner.getId(), center.getId(), spellEffect.getZone().getLength(), 7));
        send(FightPacketFormatter.localTrapDeleteMessage(owner.getId(), center.getId()));
        fight.getTraps().remove(center.getId());
    }

    @Override
    public void check(Fighter fighter) {

    }

    @Override
    public void onTrapped(Fighter fighter) {
        if (!containCell(fighter.getFightCell().getId()))
            this.cells.add(fighter.getFightCell());

        fight.send(FightPacketFormatter.trapUsedMessage(fighter.getId(), originalSpell.getId(), center.getId(), owner.getId()));

        hide();

        applyEffect(fighter);
    }

    @Override
    protected void applyEffect(Fighter fighter) {
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
