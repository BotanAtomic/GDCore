package org.graviton.game.spell;

import lombok.Data;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.common.SpellEffects;
import org.graviton.game.spell.zone.Zone;
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
        fight.setToWait(0);

        Collection<SpellEffect> effects = null;

        if (Utils.random(1, fighter.getRate(failureRate, false)) == 1) {  // Critical failure !
            fight.send(FightPacketFormatter.actionMessage(FightAction.CRITICAL_FAILURE, fighter.getId(), String.valueOf(template.getId())));
            if (endsTurnOnFailure)
                fighter.getTurn().end(true);
        } else {
            Fighter fighterTarget = fight.getFighter(target.getFirstCreature());
            fighter.getSpellCounter().addLaunchedSpell(this.template.getId(), (fighterTarget == null ? -1 : fighterTarget.getId()));

            if (turns > 0)
                fighter.getSpellCounter().getSpellTime().put(template.getId(), turns);

            fight.send(FightPacketFormatter.actionMessage(FightAction.USE_SPELL, fighter.getId(), buildData(target.getId())));
            effects = this.effects;

            if (Utils.random(1, fighter.getRate(criticalRate, true)) == 1) { // Critical success !
                fight.send(FightPacketFormatter.actionMessage(FightAction.CRITICAL_SPELL, fighter.getId(), buildData(target.getId())));
                effects = this.criticalEffects;
            }
        }

        if (effects != null) {
            Collection<SpellEffect> randomEffects;
            if (!(randomEffects = effects.stream().filter(effect -> effect.getChance() > 0).collect(Collectors.toList())).isEmpty()) {
                Optional<SpellEffect> optionalEffect = randomEffects.stream().filter(effect -> Utils.random(1, 100) <= effect.getChance()).findFirst();
                SpellEffect randomEffect = optionalEffect.isPresent() ? optionalEffect.get() : Utils.getRandomObject(randomEffects);
                randomEffect.getType().apply(fighter, randomEffect.getZone().getTargets(target, fighter), target, randomEffect);
            }

            effects.stream().filter(effect -> effect.getChance() == 0).forEach(effect -> effect.getType().apply(fighter, effect.getZone().getTargets(target, fighter), target, effect));
        }

        fighter.setCurrentActionPoint((byte) (fighter.getCurrentActionPoint() - this.actionPointCost));
        fight.send(FightPacketFormatter.actionPointEventMessage(fighter.getId(), (byte) (this.actionPointCost * -1)));

        fighter.getToExecute().forEach(Runnable::run);
        fighter.getToExecute().clear();
    }


    private String buildData(short cell) {
        return template.getId() + "," + cell + "," + template.getSprite() + "," + level + "," + template.getSpriteInformation();
    }

    boolean isGlyphEffect() {
        return this.template.getType() == 4;
    }

    boolean isBuffEffect() {
        return this.template.getType() == 1;
    }

    boolean isInvocationEffect() {
        return this.effects.stream().filter(spellEffect -> spellEffect.getType().isInvocation()).count() > 0;
    }

    public boolean isPushEffect() {
        return this.effects.stream().filter(spellEffect -> spellEffect.getType().isPush()).count() > 0;
    }

    public boolean isMultipleZone() {
        return this.effects.stream().filter(effect -> !"Pa".equals(effect.getZone().getValue())).count() > 0;
    }

    public boolean canCauseDamage() {
        return effects.stream().filter(effect -> Utils.range(effect.getType().value(), 85, 100)).count() > 0;
    }

    public boolean canHeal() {
        return effects.stream().filter(effect -> effect.getType() == SpellEffects.Heal).count() > 0;
    }

    public Zone zone() {
        Optional<Zone> zone = this.effects.stream().filter(effect -> effect.getZone() != null).map(SpellEffect::getZone).findAny();
        return zone.isPresent() ? zone.get() : null;
    }
}
