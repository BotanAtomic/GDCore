package org.graviton.game.effect.type.buff;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.Buff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.Collection;


/**
 * Created by Botan on 06/01/2017. 23:29
 */
public class SacrificeEffect implements Effect {
    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            target.setSacrificed(fighter);
            fighter.getFight().send(FightPacketFormatter.fighterBuffMessage(target.getId(), effect.getType(), 0, 0, 0, 0, effect.getTurns(), effect.getSpell().getId()));
            fighter.getFight().send(FightPacketFormatter.fighterBuffMessage(fighter.getId(), effect.getType(), 0, 0, 0, 0, effect.getTurns(), effect.getSpell().getId()));

            new Buff(fighter, effect.getTurns()) {
                @Override
                public void destroy() {
                    fighter.setSacrificed(null);
                }

                @Override
                public void check() {

                }
            };

        });
    }
}
