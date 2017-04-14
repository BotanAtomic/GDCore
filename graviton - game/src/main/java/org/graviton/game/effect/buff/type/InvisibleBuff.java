package org.graviton.game.effect.buff.type;

import org.graviton.game.effect.buff.Buff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.spell.SpellEffect;
import org.graviton.network.game.protocol.FightPacketFormatter;

/**
 * Created by Botan on 29/12/2016. 19:57
 */
public class InvisibleBuff extends Buff {

    public InvisibleBuff(Fighter fighter, SpellEffect spellEffect, short remainingTurn) {
        super(fighter, (short) (remainingTurn + 1));
        this.fighter = fighter;

        System.err.println("Set invisible for " + remainingTurn);
        fighter.getFight().send(FightPacketFormatter.fighterBuffMessage(fighter.getId(), spellEffect.getType(), 0, spellEffect.getSecond(), 0, 0, super.remainingTurns, spellEffect.getSpellId()));
        fighter.setVisible(false, (short) (remainingTurns + 1));
    }

    @Override
    public void destroy() {
        fighter.setVisible(true, (short) 0);
    }

    @Override
    public void clear() {
        destroy();
    }

    @Override
    public void check() {

    }

}
