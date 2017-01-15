package org.graviton.game.effect.type.buff;

import org.graviton.game.effect.Effect;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;

import java.util.Collection;

import static org.graviton.network.game.protocol.FightPacketFormatter.actionMessage;

/**
 * Created by Botan on 04/01/2017. 18:31
 */
public class SkinEffect implements Effect {

    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            short skin = effect.getThird() == -1 ? target.getCreature().look().getSkin() : effect.getThird();
            short turns = effect.getThird() == -1 ? 0 : effect.getTurns();
            fighter.getFight().send(actionMessage((short) effect.getType().value(), fighter.getId(), target.getId(), target.getCreature().look().getSkin(), skin, turns));
        });
    }

    @Override
    public Effect copy() {
        return new SkinEffect();
    }
}
