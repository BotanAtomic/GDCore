package org.graviton.database.entity;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.graviton.api.Manageable;
import org.graviton.core.Program;
import org.graviton.core.loader.FastLoader;
import org.graviton.database.AbstractDatabase;
import org.graviton.database.GameDatabase;
import org.graviton.database.repository.GameMapRepository;
import org.graviton.database.repository.PlayerRepository;
import org.graviton.game.area.Area;
import org.graviton.game.area.SubArea;
import org.graviton.game.creature.monster.MonsterTemplate;
import org.graviton.game.creature.npc.NpcAnswer;
import org.graviton.game.creature.npc.NpcQuestion;
import org.graviton.game.creature.npc.NpcTemplate;
import org.graviton.game.experience.Experience;
import org.graviton.game.items.Panoply;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.game.maps.GameMap;
import org.graviton.xml.XMLFile;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.graviton.constant.XMLPath.*;
import static org.graviton.database.jooq.game.tables.Items.ITEMS;

/**
 * Created by Botan on 11/11/2016 : 22:42
 */
@Slf4j
@Data
public class EntityFactory implements Manageable {
    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    //static data
    private final Map<Short, Experience> experiences = new ConcurrentHashMap<>();

    private final Map<Integer, NpcTemplate> npcTemplates = new ConcurrentHashMap<>();
    private final Map<Short, NpcQuestion> npcQuestions = new ConcurrentHashMap<>();
    private final Map<Short, NpcAnswer> npcAnswers = new ConcurrentHashMap<>();

    private final Map<Integer, MonsterTemplate> monsterTemplates = new ConcurrentHashMap<>();

    private final Map<Short, ItemTemplate> itemTemplates = new ConcurrentHashMap<>();

    private final Map<Short, Panoply> panoply = new ConcurrentHashMap<>();

    private final Map<Byte, Area> area = new ConcurrentHashMap<>();
    private final Map<Short, SubArea> subArea = new ConcurrentHashMap<>();


    private AtomicInteger itemIdentityGenerator;

    @Inject
    private GameMapRepository gameMapRepository;

    @Inject
    private PlayerRepository playerRepository;

    private GameDatabase database;

    @Inject
    public EntityFactory(Program program, @Named("database.game") AbstractDatabase database) {
        program.register(this);
        this.database = (GameDatabase) database;
    }

    private void loadArea() {
        get(AREA).getElementsByTagName("Area").forEach(element -> this.area.put(element.getAttribute("id").toByte(), new Area(element)));
        log.debug("Successfully load {} area", this.area.size());

        get(SUBAREA).getElementsByTagName("SubArea").forEach(element -> {
            Area area = this.area.get(element.getElementByTagName("area").toByte());
            this.subArea.put(element.getAttribute("id").toShort(), new SubArea(element, area));
        });

        log.debug("Successfully load {} sub area", this.subArea.size());
    }

    private void loadExperiences() {
        get(EXPERIENCE).getElementsByTagName("experience").forEach(element -> this.experiences.put(element.getAttribute("level").toShort(), new Experience(element)));

        this.experiences.keySet().forEach(i -> {
            Experience experience = this.experiences.get(i);
            experience.setNext(this.experiences.get((short) (i + 1)));
        });

        log.debug("Successfully load {} experiences data", this.experiences.size());
    }

    private void loadNpcTemplates() {
        get(NPC_TEMPLATE).getElementsByTagName("NpcTemplate").forEach(element -> this.npcTemplates.put(element.getAttribute("id").toInt(), new NpcTemplate(element)));
        log.debug("Successfully load {} npc templates", this.npcTemplates.size());
    }

    private void loadNpcData() {
        get(NPC_ANSWER).getElementsByTagName("NpcAnswer").forEach(element -> this.npcAnswers.put(element.getAttribute("id").toShort(), new NpcAnswer(element)));
        log.debug("Successfully load {} npc answers", this.npcAnswers.size());

        get(NPC_QUESTION).getElementsByTagName("NpcQuestion").forEach(element -> this.npcQuestions.put(element.getAttribute("id").toShort(), new NpcQuestion(element, this.npcAnswers)));
        log.debug("Successfully load {} npc questions", this.npcQuestions.size());
    }

    private void loadMonsterTemplates() {
        get(MONSTER_TEMPLATE).getElementsByTagName("MonsterTemplate").forEach(element -> this.monsterTemplates.put(element.getAttribute("id").toInt(), new MonsterTemplate(element)));
        log.debug("Successfully load {} monster templates", this.monsterTemplates.size());
    }

    private void loadItemTemplates() {
        get(ITEM_TEMPLATE).getElementsByTagName("item").forEach(element -> {
            ItemTemplate template = new ItemTemplate(element);
            this.itemTemplates.put(template.getId(), template);
        });

        log.debug("Successfully load {} item templates", this.itemTemplates.size());
    }

    private void loadPanoplyTemplates() {
        get(PANOPLY_TEMPLATE).getElementsByTagName("Panoply").forEach(element -> {
            Map<Short, ItemTemplate> templates = new HashMap<>();
            for (String item : element.getElementByTagName("items").toString().split(",")) {
                short template = Short.parseShort(item);
                templates.put(template, this.itemTemplates.get(template));
            }

            this.panoply.put(element.getAttribute("id").toShort(), new Panoply(element, templates));
        });

        log.debug("Successfully load {} panoply", this.panoply.size());
    }

    @Override
    public void start() {
        this.itemIdentityGenerator = new AtomicInteger(database.getNextId(ITEMS, ITEMS.ID));
        new FastLoader(this::loadExperiences,
                this::loadArea,
                this::loadItemTemplates,
                this::loadNpcData,
                this::loadNpcTemplates,
                this::loadMonsterTemplates,
                this::loadPanoplyTemplates).launch();
    }


    @Override
    public void stop() {
        playerRepository.save();
    }

    private XMLFile get(String path) {
        try {
            return new XMLFile(documentBuilderFactory.newDocumentBuilder().parse(new File("data/" + path)));
        } catch (Exception e) {
            throw new NullPointerException("File " + path + " was not found");
        }
    }

    public Experience getExperience(short level) {
        return this.experiences.get(level);
    }

    public NpcTemplate getNpcTemplate(int id) {
        return this.npcTemplates.get(id);
    }

    public MonsterTemplate getMonsterTemplate(int id) {
        return this.monsterTemplates.get(id);
    }

    public GameMap getMap(int id) {
        return gameMapRepository.get(id);
    }

    public GameMap getMapByPosition(String position) {
        return this.gameMapRepository.getByPosition(position);
    }

    public ItemTemplate getItemTemplate(short id) {
        return this.itemTemplates.get(id);
    }

    public int getNextItemId() {
        return this.itemIdentityGenerator.getAndIncrement();
    }
}
