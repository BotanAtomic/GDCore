package org.graviton.game.effect.buff.type;

import org.graviton.game.effect.buff.Buff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.spell.SpellEffect;
import org.graviton.network.game.protocol.FightPacketFormatter;

/**
 * Created by Botan on 08/01/2017. 13:48
 */
public class ReturnSpellBuff extends Buff {
    private final SpellEffect spellEffect;

    public ReturnSpellBuff(Fighter fighter, SpellEffect spellEffect) {
        super(fighter, spellEffect.getTurns());
        this.spellEffect = spellEffect;
        fighter.setReturnSpell((byte) (fighter.getReturnSpell() + spellEffect.getSecond()));
        fighter.getFight().send(FightPacketFormatter.fighterBuffMessage(fighter.getId(), spellEffect.getType(), 0, spellEffect.getSecond(), 0, 0, super.remainingTurns, spellEffect.getSpellId()));
    }

    @Override
    public void destroy() {
        fighter.setReturnSpell((byte) (fighter.getReturnSpell() - spellEffect.getSecond()));
    }

    @Override
    public void clear() {
        destroy();
        fighter.getFight().send(FightPacketFormatter.fighterBuffMessage(fighter.getId(), spellEffect.getType(), 0, spellEffect.getSecond(), 0, 0, (short) 0, spellEffect.getSpellId()));
    }

    @Override
    public void check() {

    }
}
