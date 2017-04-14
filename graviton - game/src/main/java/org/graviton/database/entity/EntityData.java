package org.graviton.database.entity;

import lombok.Data;
import org.graviton.game.action.fight.AbstractFightAction;
import org.graviton.game.area.Area;
import org.graviton.game.area.SubArea;
import org.graviton.game.creature.monster.MonsterTemplate;
import org.graviton.game.creature.monster.extra.ExtraMonster;
import org.graviton.game.creature.npc.NpcAnswer;
import org.graviton.game.creature.npc.NpcQuestion;
import org.graviton.game.creature.npc.NpcTemplate;
import org.graviton.game.drop.Drop;
import org.graviton.game.experience.Experience;
import org.graviton.game.house.HouseTemplate;
import org.graviton.game.items.Panoply;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.game.maps.object.InteractiveObjectTemplate;
import org.graviton.game.spell.SpellTemplate;
import org.graviton.game.zaap.Zaap;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Botan on 13/12/2016. 21:13
 */

@Data
class EntityData {
    final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    final Map<Short, Experience> experiences = new ConcurrentHashMap<>();
    final Map<Integer, NpcTemplate> npcTemplates = new ConcurrentHashMap<>();
    final Map<Short, NpcQuestion> npcQuestions = new ConcurrentHashMap<>();
    final List<NpcAnswer> npcAnswers = Collections.synchronizedList(new ArrayList<>());
    final Map<Integer, MonsterTemplate> monsterTemplates = new ConcurrentHashMap<>();
    final Map<Integer, ExtraMonster> extraMonsters = new ConcurrentHashMap<>();
    final Map<Short, ItemTemplate> itemTemplates = new ConcurrentHashMap<>();
    final Map<Short, Panoply> panoply = new ConcurrentHashMap<>();
    final Map<Short, Area> area = new ConcurrentHashMap<>();
    final Map<Short, SubArea> subArea = new ConcurrentHashMap<>();
    final Map<Short, SpellTemplate> spells = new ConcurrentHashMap<>();
    final Map<Integer, InteractiveObjectTemplate> interactiveObjects = new ConcurrentHashMap<>();
    final List<Drop> drops = Collections.synchronizedList(new ArrayList<>());
    final Map<Short, HouseTemplate> houses = new ConcurrentHashMap<>();
    final Map<Integer, AbstractFightAction> fightAction = new ConcurrentHashMap<>();
    final Map<Integer, Zaap> zaaps = new ConcurrentHashMap<>();

    public NpcTemplate getNpcTemplate(int id) {
        return this.npcTemplates.get(id);
    }

    public List<NpcAnswer> getNpcAnswer(short id) {
        return this.npcAnswers.stream().filter(answer -> answer.getId() == id).collect(Collectors.toList());
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

    public List<Drop> getDrops() {
        return this.drops;
    }

    public InteractiveObjectTemplate getInteractiveObject(int id) {
        return this.interactiveObjects.get(id);
    }

    public AbstractFightAction getFightAction(int map) {
        return this.fightAction.get(map);
    }

}
