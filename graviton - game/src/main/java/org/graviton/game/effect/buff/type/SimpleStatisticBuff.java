package org.graviton.game.effect.buff.type;

import org.graviton.game.effect.buff.Buff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.network.game.protocol.FightPacketFormatter;


/**
 * Created by Botan on 28/12/2016. 23:53
 */
public class SimpleStatisticBuff extends Buff {
    private final SpellEffect spellEffect;
    private final CharacteristicType characteristicType;
    private int value;
    private boolean add;

    public SimpleStatisticBuff(CharacteristicType characteristicType, boolean add, Fighter fighter, int value, SpellEffect spellEffect, short remainingTurns) {
        super(fighter, remainingTurns);
        this.characteristicType = characteristicType;
        this.fighter = fighter;
        this.spellEffect = spellEffect;
        this.value = value;
        this.add = add;
        create();
    }

    private void create() {
        fighter.getFight().send(FightPacketFormatter.fighterBuffMessage(fighter.getId(), spellEffect.getType(), add ? value : value * -1, 0, 0, 0, (short) (super.remainingTurns - 1), spellEffect.getSpellId()));

        if (characteristicType != CharacteristicType.Vitality) {
            if (characteristicType == CharacteristicType.ActionPoints)
                fighter.addActionPoint((byte) value);
            else if (characteristicType == CharacteristicType.MovementPoints)
                fighter.addMovementPoint((byte) value);

            fighter.getStatistics().get(characteristicType).addContext((short) (value));
        } else {
            fighter.getLife().addMaximum(value);
            fighter.getLife().add(value);
        }

        fighter.refreshStatistics();

    }

    @Override
    public void destroy() {
        if (characteristicType != CharacteristicType.Vitality)
            fighter.getStatistics().get(characteristicType).addContext((short) (-value));
        else {
            fighter.getLife().addMaximum(-value);
            fighter.getLife().remove(value);
        }

        if (characteristicType == CharacteristicType.MovementPoints || characteristicType == CharacteristicType.ActionPoints) {
            fighter.getFight().send(FightPacketFormatter.fighterInformationMessage(fighter.getFight().fighters()));
            fighter.initializeFighterPoints();
        }

        fighter.refreshStatistics();
    }

    @Override
    public void clear() {
        destroy();
        fighter.getFight().send(FightPacketFormatter.fighterBuffMessage(fighter.getId(), spellEffect.getType(), add ? value : value * -1, 0, 0, 0, (short) 0, spellEffect.getSpellId()));
    }

    @Override
    public void check() {

    }
}
