package org.graviton.game.items.weapon.heal;

import org.graviton.game.effect.Effect;
import org.graviton.game.items.common.Bonus;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.game.items.weapon.WeaponEffect;
import org.graviton.game.spell.SpellEffect;

/**
 * Created by Botan on 15/01/2017. 13:45
 */
public class HealEffect implements WeaponEffect {
    private Bonus bonus;

    @Override
    public SpellEffect build(ItemTemplate item, short criticalBonus) {
        return new SpellEffect(-1) {{
            setDice(bonus);
            setThird(criticalBonus);
        }};
    }

    @Override
    public Effect effect() {
        return new org.graviton.game.effect.type.damage.HealEffect();
    }

    @Override
    public WeaponEffect setBonus(Bonus bonus) {
        this.bonus = bonus;
        return this;
    }

    @Override
    public WeaponEffect copy() {
        return new HealEffect();
    }
}
