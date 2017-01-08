package org.graviton.game.effect.buff.type;

import org.graviton.game.effect.buff.Buff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.spell.SpellEffect;
import org.graviton.network.game.protocol.FightPacketFormatter;

/**
 * Created by Botan on 07/01/2017. 15:52
 */
public class DamageSufferBuff extends Buff {
    private final short bonus;

    public DamageSufferBuff(Fighter fighter, SpellEffect spellEffect) {
        super(fighter, spellEffect.getTurns());
        this.bonus = spellEffect.getFirst();
        fighter.setDamageSuffer((short) (fighter.getDamageSuffer() + this.bonus));
        fighter.getFight().send(FightPacketFormatter.fighterBuffMessage(fighter.getId(), spellEffect.getType(), spellEffect.getFirst(), 0, 0, 0, super.remainingTurns, spellEffect.getSpellId()));

    }

    @Override
    public void destroy() {
        fighter.setDamageSuffer((short) (fighter.getDamageSuffer() - this.bonus));
    }

    @Override
    public void check() {

    }
}
