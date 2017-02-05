package org.graviton.game.trap;

import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.Spell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.spell.SpellTemplate;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.Collections;
import java.util.List;

/**
 * Created by Botan on 08/01/2017. 19:16
 */
public class Glyph extends AbstractTrap {
    private short turns;

    public Glyph(Fighter owner, SpellTemplate originalSpell, SpellEffect spellEffect, Spell spell, List<Cell> cells) {
        super(owner, originalSpell, spellEffect.getThird(), spellEffect, spell, cells);
        this.turns = spellEffect.getTurns();
    }

    @Override
    public void onTrapped(Fighter fighter) {

    }

    @Override
    protected void applyEffect(Fighter fighter) {
        spell.getEffects().forEach(effect -> effect.getType().apply(owner, Collections.singletonList(fighter), center, effect));
    }

    @Override
    void show() {
        send(FightPacketFormatter.trapAddedMessage(owner.getId(), center.getId(), spellEffect.getZone().getLength(), this.color));
        fight.getTraps().put(center.getId(), this);
    }

    @Override
    void hide() {
        send(FightPacketFormatter.trapDeletedMessage(owner.getId(), center.getId(), spellEffect.getZone().getLength(), this.color));
        fight.getTraps().remove(center.getId());
    }

    @Override
    public void check(Fighter fighter) {
        if (fighter.getId() == owner.getId()) {
            turns--;
            if (turns == 0) {
                hide();
                return;
            }
        }

        if (containCell(fighter.getFightCell().getId()))
            applyEffect(fighter);
    }

    private void send(String data) {
        fight.send(data);
    }

}
