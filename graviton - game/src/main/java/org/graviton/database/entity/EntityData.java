package org.graviton.database.entity;

import org.graviton.game.area.Area;
import org.graviton.game.area.SubArea;
import org.graviton.game.creature.monster.MonsterTemplate;
import org.graviton.game.creature.monster.extra.ExtraMonster;
import org.graviton.game.creature.npc.NpcAnswer;
import org.graviton.game.creature.npc.NpcQuestion;
import org.graviton.game.creature.npc.NpcTemplate;
import org.graviton.game.experience.Experience;
import org.graviton.game.items.Panoply;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.game.spell.SpellTemplate;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Botan on 13/12/2016. 21:13
 */
class EntityData {
    final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    final Map<Short, Experience> experiences = new ConcurrentHashMap<>();

    final Map<Integer, NpcTemplate> npcTemplates = new ConcurrentHashMap<>();
    final Map<Short, NpcQuestion> npcQuestions = new ConcurrentHashMap<>();
    final Map<Short, NpcAnswer> npcAnswers = new ConcurrentHashMap<>();

    final Map<Integer, MonsterTemplate> monsterTemplates = new ConcurrentHashMap<>();
    final Map<Integer, ExtraMonster> extraMonsters = new ConcurrentHashMap<>();

    final Map<Short, ItemTemplate> itemTemplates = new ConcurrentHashMap<>();

    final Map<Short, Panoply> panoply = new ConcurrentHashMap<>();

    final Map<Short, Area> area = new ConcurrentHashMap<>();
    final Map<Short, SubArea> subArea = new ConcurrentHashMap<>();

    final Map<Short, SpellTemplate> spells = new ConcurrentHashMap<>();

    public NpcTemplate getNpcTemplate(int id) {
        return this.npcTemplates.get(id);
    }

    public NpcAnswer getNpcAnswer(short id) {
        return this.npcAnswers.get(id);
    }

    public NpcQuestion getNpcQuestion(short id) {
        return this.npcQuestions.get(id);
    }

    public Experience getExperience(short level) {
        return this.experiences.get(level);
    }

    public MonsterTemplate getMonsterTemplate(int id) {
        return this.monsterTemplates.get(id);
    }

    public ItemTemplate getItemTemplate(short id) {
        return this.itemTemplates.get(id);
    }

    public SubArea getSubArea(short id) {
        return this.subArea.get(id);
    }

    public SpellTemplate getSpellTemplate(short spell) {
        return this.spells.get(spell);
    }

}
