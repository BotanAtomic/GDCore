package org.graviton.game.effect.buff.type;

import org.graviton.game.effect.buff.Buff;
import org.graviton.game.fight.Fighter;
import org.graviton.game.spell.SpellEffect;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.Collections;

import static org.graviton.utils.Utils.limit;

/**
 * Created by Botan on 07/01/2017. 12:41
 */
public class PunishmentBuff extends Buff {
    private final SpellEffect effect;
    private final SpellEffect bonusEffect;

    private short bonus;
    private short maximum;

    public PunishmentBuff(Fighter fighter, SpellEffect baseEffect) {
        super(fighter, baseEffect.getTurns());
        this.effect = baseEffect;
        this.bonusEffect = build(baseEffect);
        this.maximum = baseEffect.getSecond();
        fighter.getFight().send(FightPacketFormatter.fighterBuffMessage(fighter.getId(), baseEffect.getType(), 0, baseEffect.getSecond(), baseEffect.getTurns(), 0, super.remainingTurns, baseEffect.getSpellId()));
    }

    @Override
    public void destroy() {

    }

    @Override
    public void clear() {
        fighter.getFight().send(FightPacketFormatter.fighterBuffMessage(fighter.getId(), effect.getType(), 0, effect.getSecond(), effect.getTurns(), 0, (short) 0, effect.getSpellId()));
    }

    @Override
    public void check() {
        bonus = 0;
    }

    public void add(short value) {
        value = (short) limit(value, maximum - bonus);

        if (value + this.bonus <= maximum && value > 0) {
            bonusEffect.setFirst(value);
            bonusEffect.getType().apply(fighter, Collections.singletonList(fighter), null, bonusEffect);
            this.bonus += value;
        }
    }

    private SpellEffect build(SpellEffect baseEffect) {
        return new SpellEffect(baseEffect.getFirst()) {{
            setSecond((short) -1);
            setFirst((short) 0);
            setChance((short) 0);
            setSpell(baseEffect.getSpell());
            setTurns(baseEffect.getThird());
            setThird((short) 1);
        }};
    }


}
