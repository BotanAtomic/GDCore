package org.graviton.game.items.weapon.damage;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.enums.DamageType;
import org.graviton.game.items.common.Bonus;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.game.items.weapon.WeaponEffect;
import org.graviton.game.spell.SpellEffect;

/**
 * Created by Botan on 14/01/2017. 19:49
 */
public class DamageEffect implements WeaponEffect {
    private final DamageType damageType;
    private Bonus bonus;

    public DamageEffect(DamageType damageType) {
        this.damageType = damageType;
    }

    public DamageEffect setBonus(Bonus bonus) {
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
        return new org.graviton.game.effect.type.damage.DamageEffect(damageType);
    }

    @Override
    public WeaponEffect copy() {
        return new DamageEffect(damageType);
    }
}
