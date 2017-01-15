package org.graviton.game.items.weapon;

import org.graviton.game.effect.Effect;
import org.graviton.game.items.common.Bonus;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.game.spell.SpellEffect;

/**
 * Created by Botan on 14/01/2017. 19:49
 */
public interface WeaponEffect {

    SpellEffect build(ItemTemplate item, short criticalBonus);

    Effect effect();

    WeaponEffect setBonus(Bonus bonus);

    WeaponEffect copy();
}
