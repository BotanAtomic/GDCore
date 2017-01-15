package org.graviton.game.effect.buff.type;

import org.graviton.game.effect.buff.Buff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.spell.SpellTemplate;
import org.graviton.network.game.protocol.FightPacketFormatter;

/**
 * Created by Botan on 01/01/2017. 22:18
 */
public class SpellBoostBuff extends Buff {
    private static int MAXIMUM = 3;

    private final SpellTemplate spellTemplate;
    private final SpellEffect spellEffect;

    public SpellBoostBuff(Fighter fighter, SpellTemplate spellTemplate, SpellEffect spellEffect, short remainingTurns) {
        super(fighter, remainingTurns);
        this.spellTemplate = spellTemplate;
        this.fighter = fighter;
        this.spellEffect = spellEffect;

        fighter.getFight().send(FightPacketFormatter.fighterBuffMessage(fighter.getId(), spellEffect.getType(), spellTemplate.getId(), spellEffect.getThird(), 0, 0, (short) (super.remainingTurns - 1), spellEffect.getSpellId()));
    }

    @Override
    public void destroy() {
        if (!fighter.hasLaunchedSpell(spellTemplate.getId()))
            fighter.removeSpellBoost(spellTemplate.getId());
    }

    @Override
    public void clear() {

    }

    @Override
    public void check() {
        if (remainingTurns == 1) {
            if (((fighter.getSpellBoost(this.spellTemplate.getId()) + spellEffect.getThird()) / MAXIMUM) < spellEffect.getThird())
                fighter.addSpellBoost(spellTemplate.getId(), spellEffect.getThird());
        }
    }
}
