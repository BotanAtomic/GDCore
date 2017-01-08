package org.graviton.game.effect.buff.type;

import lombok.Data;
import org.graviton.game.effect.buff.Buff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.spell.SpellEffect;
import org.graviton.network.game.protocol.FightPacketFormatter;

/**
 * Created by Botan on 07/01/2017. 18:43
 */

@Data
public class RandomAttackResultBuff extends Buff {
    private short rateHeal;
    private short rateDamage;

    public RandomAttackResultBuff(Fighter fighter, SpellEffect spellEffect) {
        super(fighter, spellEffect.getTurns());
        this.rateHeal = spellEffect.getSecond();
        this.rateDamage = spellEffect.getFirst();
        fighter.getFight().send(FightPacketFormatter.fighterBuffMessage(fighter.getId(), spellEffect.getType(), rateDamage, rateHeal, spellEffect.getThird(), 0, (short) (super.remainingTurns - 1), spellEffect.getSpellId()));
    }

    @Override
    public void destroy() {

    }

    @Override
    public void check() {

    }


}
