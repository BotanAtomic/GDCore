package org.graviton.game.effect.type.buff;

import org.graviton.game.effect.Effect;
import org.graviton.game.effect.buff.Buff;
import org.graviton.game.effect.buff.type.SimpleStatisticBuff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.statistics.common.CharacteristicType;

import java.util.Collection;

import static org.graviton.network.game.protocol.FightPacketFormatter.actionMessage;

/**
 * Created by Botan on 28/12/2016. 23:35
 */
public class SimpleStatisticEffect implements Effect {
    private final CharacteristicType characteristicType;
    private final boolean add;

    public SimpleStatisticEffect(CharacteristicType characteristicType, boolean add) {
        this.characteristicType = characteristicType;
        this.add = add;
    }

    private void apply(Fighter fighter, Fighter target, SpellEffect effect) {
        short value = effect.getSecond() != -1 ? effect.getDice().random() : effect.getFirst();
        short finalValue = (short) (add ? value : -value);

        fighter.getFight().send(actionMessage((short) effect.getType().value(), fighter.getId(), target.getId(), value, effect.getTurns() == 1 ? 2 : effect.getTurns()));
        new SimpleStatisticBuff(characteristicType, add, target, finalValue, effect, effect.getTurns());
    }


    @Override
    public void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect) {
        targets.forEach(target -> {
            if (effect.getTurns() > 0) {
                System.err.println("Turns = " + effect.getTurns());
                new Buff(target, effect.getTurns()) {
                    @Override
                    public void destroy() {
                        System.err.println("destroy........");
                    }

                    @Override
                    public void check() {
                        apply(fighter, target, effect);
                        System.err.println("aply........" + remainingTurns);

                    }
                };
            } else
                apply(fighter, target, effect);
        });
    }
}
