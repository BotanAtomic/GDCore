package org.graviton.game.effect.type.buff;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.Buff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.Collection;

import static org.graviton.game.spell.common.SpellEffects.Transpose_ally;

/**
 * Created by Botan on 06/01/2017. 22:11
 */
public class DodgeAttackEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            target.setDodgeAttack(true);
            fighter.getFight().send(FightPacketFormatter.fighterBuffMessage(target.getId(), Transpose_ally, effect.getFirst(), effect.getSecond(), 0, 0, effect.getTurns(), effect.getSpell().getId()));

            new Buff(target, effect.getTurns()) {
                @Override
                public void destroy() {
                    target.setDodgeAttack(false);
                    target.removeBuff(this);
                }

                @Override
                public void check() {

                }
            };
        });

    }

}
