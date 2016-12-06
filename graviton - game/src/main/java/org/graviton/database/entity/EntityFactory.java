package org.graviton.database.entity;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.graviton.api.Manageable;
import org.graviton.core.Program;
import org.graviton.database.AbstractDatabase;
import org.graviton.database.GameDatabase;
import org.graviton.database.repository.GameMapRepository;
import org.graviton.database.repository.PlayerRepository;
import org.graviton.game.creature.monster.MonsterTemplate;
import org.graviton.game.creature.npc.NpcTemplate;
import org.graviton.game.experience.Experience;
import org.graviton.game.items.Panoply;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.game.maps.GameMap;
import org.jooq.Record;
import org.jooq.Result;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.graviton.database.jooq.game.tables.Items.ITEMS;
import static org.graviton.database.jooq.game.tables.Panoply.PANOPLY;

/**
 * Created by Botan on 11/11/2016 : 22:42
 */
@Slf4j
@Data
public class EntityFactory implements Manageable {
    private final static String experiencePath = "experiences/values.xml";

    private final static String npcTemplatePath = "npc/templates.xml";

    private final static String itemTemplatePath = "item/templates.xml";

    private final static String monsterTemplatePath = "monster/templates.xml";


    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    //static data
    private final Map<Short, Experience> experiences = new ConcurrentHashMap<>();

    private final Map<Integer, NpcTemplate> npcTemplates = new ConcurrentHashMap<>();

    private final Map<Integer, MonsterTemplate> monsterTemplates = new ConcurrentHashMap<>();

    private final Map<Short, ItemTemplate> itemTemplates = new ConcurrentHashMap<>();

    private final Map<Short, Panoply> panoply = new ConcurrentHashMap<>();

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

    private void loadExperiences() {
        Document document = get(experiencePath);

        if (document == null)
            throw new NullPointerException("File " + experiencePath + " was not found");

        NodeList nodeList = document.getElementsByTagName("experience");

        IntStream.range(0, nodeList.getLength()).forEach(i -> {
            Element element = (Element) nodeList.item(i);

            this.experiences.put(Short.parseShort(element.getAttribute("level")), new Experience(element));
        });

        this.experiences.keySet().forEach(i -> {
            Experience experience = this.experiences.get(i);
            experience.setNext(this.experiences.get((short) (i + 1)));
        });

        log.debug("Successfully load {} experiences data", this.experiences.size());
    }

    private void loadNpcTemplates() {
        Document document = get(npcTemplatePath);

        if (document == null)
            throw new NullPointerException("File " + npcTemplatePath + " was not found");

        NodeList nodeList = document.getElementsByTagName("NpcTemplate");

        IntStream.range(0, nodeList.getLength()).forEach(i -> {
            Element element = (Element) nodeList.item(i);
            this.npcTemplates.put(Integer.parseInt(element.getAttribute("id")), new NpcTemplate(element));
        });

        log.debug("Successfully load {} npc templates", this.npcTemplates.size());
    }

    private void loadMonsterTemplates() {
        Document document = get(monsterTemplatePath);

        if (document == null)
            throw new NullPointerException("File " + monsterTemplatePath + " was not found");

        NodeList nodeList = document.getElementsByTagName("MonsterTemplate");

        IntStream.range(0, nodeList.getLength()).forEach(i -> {
            Element element = (Element) nodeList.item(i);
            this.monsterTemplates.put(Integer.parseInt(element.getAttribute("id")), new MonsterTemplate(element));
        });

        log.debug("Successfully load {} monster templates", this.monsterTemplates.size());
    }

    private void loadItemsTemplate() {

        Document document = get(itemTemplatePath);

        if (document == null)
            throw new NullPointerException("File " + itemTemplatePath + " was not found");

        NodeList nodeList = document.getElementsByTagName("item");

        IntStream.range(0, nodeList.getLength()).forEach(i -> {
            Element element = (Element) nodeList.item(i);
            ItemTemplate template = new ItemTemplate(element);
            this.itemTemplates.put(template.getId(), template);
        });


        log.debug("Successfully load {} item templates", this.itemTemplates.size());

        Result<Record> result = database.getResult(PANOPLY);
        result.forEach(record -> {
            Map<Short, ItemTemplate> templates = new HashMap<>();

            for (String item : record.get(PANOPLY.ITEMS).split(",")) {
                short template = Short.parseShort(item);
                templates.put(template, this.itemTemplates.get(template));
            }
            this.panoply.put(record.get(PANOPLY.ID), new Panoply(record, templates));
        });

        log.debug("Successfully load {} panoply", this.panoply.size());
    }

    @Override
    public void start() {
        this.itemIdentityGenerator = new AtomicInteger(database.getNextId(ITEMS, ITEMS.ID));

        loadExperiences();
        loadNpcTemplates();
        loadMonsterTemplates();
        loadItemsTemplate();
    }

    @Override
    public void stop() {
        playerRepository.save();
    }

    private Document get(String path) {
        try {
            return documentBuilderFactory.newDocumentBuilder().parse(new File("data/" + path));
        } catch (Exception e) {
            log.error("cannot get document {} : {}", path, e);
        }
        return null;
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
