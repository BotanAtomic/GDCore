package org.graviton.game.effect.buff.type;

import org.graviton.game.effect.buff.Buff;
import org.graviton.game.effect.enums.DamageType;
import org.graviton.game.effect.type.damage.DamageEffect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.spell.SpellEffect;

/**
 * Created by Botan on 02/01/2017. 00:45
 */
public class PoisonBuff extends Buff {
    private final DamageType damageType;
    private final SpellEffect effect;

    public PoisonBuff(Fighter fighter, DamageType damageType, SpellEffect spellEffect, short remainingTurns) {
        super(fighter, remainingTurns);
        this.damageType = damageType;
        this.effect = spellEffect;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void check() {
        fighter.getFight().hit(fighter, fighter, DamageEffect.damage(effect, fighter, fighter, this.damageType, 0));
    }
}
