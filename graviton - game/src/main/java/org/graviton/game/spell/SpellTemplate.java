package org.graviton.game.spell;

import lombok.Data;
import org.graviton.constant.Dofus;
import org.graviton.game.items.common.Bonus;
import org.graviton.xml.XMLElement;

/**
 * Created by Botan on 24/12/2016. 17:38
 */

@Data
public class SpellTemplate {
    private final short id;
    private final short sprite;
    private final String spriteInformation;
    private final byte type;
    private final short duration;

    private Spell[] spells = new Spell[Dofus.MAX_SPELL_LEVEL];

    public SpellTemplate(XMLElement element) {
        this.id = element.getAttribute("id").toShort();
        this.sprite = element.getElementByTagName("sprite", "id").toShort();
        this.spriteInformation = element.getElementByTagName("sprite", "infos").toString();
        this.type = element.getAttribute("type").toByte();
        this.duration = element.getAttribute("duration").toShort();


        element.getElementsByTagName("level").forEach(spell -> {
            Spell current = new Spell(spell.getAttribute("id").toByte(), this);
            current.setActionPointCost(spell.getAttribute("costAP").toByte());
            current.setMinimumRange(spell.getAttribute("minRange").toByte());
            current.setMaximumRange(spell.getAttribute("maxRange").toByte());
            current.setCriticalRate(spell.getAttribute("criticalRate").toShort());
            current.setFailureRate(spell.getAttribute("failureRate").toShort());
            current.setInline(spell.getAttribute("inline").toBoolean());
            current.setLos(spell.getAttribute("los").toBoolean());
            current.setEmptyCell(spell.getAttribute("emptyCell").toBoolean());
            current.setAdjustableRange(spell.getAttribute("adjustableRange").toBoolean());
            current.setEndsTurnOnFailure(spell.getAttribute("endsTurnOnFailure").toBoolean());
            current.setMaxPerTurn(spell.getAttribute("maxPerTurn").toByte());
            current.setMaxPerPlayer(spell.getAttribute("maxPerPlayer").toByte());
            current.setTurns(spell.getAttribute("turns").toByte());

            spell.getElementsByTagName("effect").forEach(effect -> {
                SpellEffect spellEffect = new SpellEffect(effect.getAttribute("type").toInt());

                spellEffect.setFirst(effect.getAttribute("first").toShort());
                spellEffect.setSecond(effect.getAttribute("second").toShort());
                spellEffect.setThird(effect.getAttribute("third").toShort());
                spellEffect.setTurns(effect.getAttribute("turns").toShort());
                spellEffect.setChance(effect.getAttribute("chance").toShort());
                spellEffect.setTarget(effect.getAttribute("target").toInt());
                spellEffect.setDice(Bonus.parseBonus(effect.getAttribute("dice").toString()));
                spellEffect.setCompiledZone(effect.getAttribute("zone").toString());
                spellEffect.setSpell(current);

                current.addEffect(spellEffect, effect.getAttribute("critical").toBoolean());

            });

            spells[current.getLevel() - 1] = current;
        });
    }

    public Spell getLevel(byte level) {
        return this.spells[level - 1];
    }

}
