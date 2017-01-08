package org.graviton.game.effect.buff.type;

import org.graviton.game.effect.buff.Buff;
import org.graviton.game.effect.type.damage.HealEffect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.spell.SpellEffect;

/**
 * Created by Botan on 07/01/2017. 22:19
 */
public class HealBuff extends Buff {
    private final SpellEffect spellEffect;

    public HealBuff(Fighter fighter, SpellEffect spellEffect) {
        super(fighter, spellEffect.getTurns());
        this.spellEffect = spellEffect;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void check() {
        HealEffect.heal(fighter, fighter, spellEffect);
    }
}
