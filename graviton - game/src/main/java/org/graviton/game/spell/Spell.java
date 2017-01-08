package org.graviton.game.spell;

import lombok.Data;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.maps.cell.Cell;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Botan on 24/12/2016. 17:40
 */

@Data
public class Spell {
    private byte level;
    private SpellTemplate template;
    private byte actionPointCost;
    private byte minimumRange, maximumRange;
    private short criticalRate, failureRate;
    private boolean inline, los, emptyCell, adjustableRange, endsTurnOnFailure;
    private byte maxPerTurn, maxPerPlayer, turns;
    private Collection<SpellEffect> effects = new ArrayList<>();
    private Collection<SpellEffect> criticalEffects = new ArrayList<>();

    Spell(byte level, SpellTemplate template) {
        this.level = level;
        this.template = template;
    }

    void addEffect(SpellEffect effect, boolean critical) {
        if (!critical)
            this.effects.add(effect);
        else
            this.criticalEffects.add(effect);
    }

    public Spell next() {
        return this.template.getLevel((byte) (level + 1));
    }

    public void applyToFight(Fighter fighter, Cell target) {
        Fight fight = fighter.getFight();

        Collection<SpellEffect> effects = null;

        if (Utils.random(1, fighter.getRate(failureRate, false)) == 1) {  // Critical failure !
            fight.send(FightPacketFormatter.actionMessage(FightAction.CRITICAL_FAILURE, fighter.getId(), String.valueOf(template.getId())));
            if (endsTurnOnFailure)
                fighter.getTurn().end();
        } else {
            fighter.addLaunchedSpell(this.template.getId());
            fight.send(FightPacketFormatter.actionMessage(FightAction.USE_SPELL, fighter.getId(), buildData(target.getId())));
            effects = this.effects;

            if (Utils.random(1, fighter.getRate(criticalRate, true)) == 1) { // Critical success !
                fight.send(FightPacketFormatter.actionMessage(FightAction.CRITICAL_SPELL, fighter.getId(), buildData(target.getId())));
                effects = this.criticalEffects;
            }
        }

        if (effects != null) {
            Collection<SpellEffect> randomEffects;
            if ((randomEffects = effects.stream().filter(effect -> effect.getChance() > 0).collect(Collectors.toList())).size() > 0) {
                Optional<SpellEffect> optionalEffect = randomEffects.stream().filter(effect -> Utils.random(1, 100) <= effect.getChance()).findFirst();
                SpellEffect randomEffect = optionalEffect.isPresent() ? optionalEffect.get() : Utils.getRandomObject(randomEffects);

                System.err.println(randomEffect.getType().name());

                randomEffect.getType().apply(fighter, randomEffect.getZone().getTargets(target, fighter), target, randomEffect);
            }

            effects.stream().filter(effect -> effect.getChance() == 0).forEach(effect -> effect.getType().apply(fighter, effect.getZone().getTargets(target, fighter), target, effect));
        }

        fighter.setCurrentActionPoint((byte) (fighter.getCurrentActionPoint() - this.actionPointCost));
        fight.send(FightPacketFormatter.actionPointEventMessage(fighter.getId(), (byte) (this.actionPointCost * -1)));
    }


    private String buildData(short cell) {
        return template.getId() + "," + cell + "," + template.getSprite() + "," + level + "," + template.getSpriteInformation();
    }

}
