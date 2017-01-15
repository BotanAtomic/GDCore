package org.graviton.game.items.weapon.damage;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.enums.DamageType;
import org.graviton.game.items.common.Bonus;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.game.items.weapon.WeaponEffect;
import org.graviton.game.spell.SpellEffect;

/**
 * Created by Botan on 15/01/2017. 13:51
 */
public class StealEffect implements WeaponEffect {
    private final DamageType damageType;
    private Bonus bonus;

    public StealEffect(DamageType damageType) {
        this.damageType = damageType;
    }

    public StealEffect setBonus(Bonus bonus) {
        this.bonus = bonus;
        return this;
    }

    @Override
    public SpellEffect build(ItemTemplate item, short criticalBonus) {
        return new SpellEffect(-1) {{
            setDice(bonus);
            setThird(criticalBonus);
        }};
    }

    @Override
    public Effect effect() {
        return new org.graviton.game.effect.type.damage.StealEffect(damageType, false);
    }

    @Override
    public WeaponEffect copy() {
        return new StealEffect(damageType);
    }
}

