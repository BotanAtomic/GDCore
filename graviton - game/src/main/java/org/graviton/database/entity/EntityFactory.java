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
import org.graviton.game.action.item.ItemAction;
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
import org.graviton.game.maps.GameMap;
import org.graviton.game.spell.SpellTemplate;
import org.graviton.utils.Utils;
import org.graviton.xml.Performer;
import org.graviton.xml.XMLElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.graviton.constant.XMLPath.*;
import static org.graviton.database.jooq.game.tables.Items.ITEMS;


/**
 * Created by Botan on 11/11/2016 : 22:42
 */
@Slf4j
@Data
public class EntityFactory extends EntityData implements Manageable {
    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
    private final ExecutorService worker = Executors.newCachedThreadPool();

    private AtomicInteger itemIdentityGenerator;
    private GameDatabase database;

    @Inject
    private GameMapRepository gameMapRepository;

    @Inject
    private PlayerRepository playerRepository;

    @Inject
    public EntityFactory(Program program, @Named("database.game") AbstractDatabase database) {
        program.register(this);
        this.database = (GameDatabase) database;
    }

    private void loadSpells() {
        long time = System.currentTimeMillis() / 1000;

        NodeList list = get(SPELLS).getElementsByTagName("spell");
        apply(list, element -> this.spells.put(element.getAttribute("id").toShort(), new SpellTemplate(element)));

        log.debug("Successfully load {} spells in {}", this.spells.size(), System.currentTimeMillis() / 1000 - time);
    }

    private void loadGameMaps() {
        long time = System.currentTimeMillis() / 1000;
        apply(get(AREA).getElementsByTagName("Area"), (element -> this.area.put(element.getAttribute("id").toShort(), new Area(element))));
        log.debug("Successfully load {} area in {}", this.area.size(), System.currentTimeMillis() / 1000 - time);

        time = System.currentTimeMillis() / 1000;
        apply(get(SUBAREA).getElementsByTagName("SubArea"), (element -> this.subArea.put(element.getAttribute("id").toShort(), new SubArea(element, this.area.get(element.getElementByTagName("area").toShort())))));

        log.debug("Successfully load {} sub area in {}", this.subArea.size(), System.currentTimeMillis() / 1000 - time);

        time = System.currentTimeMillis() / 1000;
        log.debug("Successfully load {} game map in {}", gameMapRepository.load(get(MAPS)), System.currentTimeMillis() / 1000 - time);

        time = System.currentTimeMillis() / 1000;
        log.debug("Successfully register {} npc in {}", gameMapRepository.loadNpc(get(NPCS)), System.currentTimeMillis() / 1000 - time);

        loadExtraMonsters();
    }

    private void loadExtraMonsters() {
        long time = System.currentTimeMillis() / 1000;

        apply(get(EXTRA_MONSTERS).getElementsByTagName("ExtraMonster"), element -> {
            int id = element.getAttribute("id").toInt();
            ExtraMonster extraMonster = new ExtraMonster(getMonsterTemplate(id), element.getElementByTagName("chance").toByte());

            for (String subArea : element.getElementByTagName("subarea").toString().split(","))
                extraMonster.registerSubArea(Short.parseShort(subArea));

            extraMonsters.put(id, extraMonster);
        });

        log.debug("Successfully load {} extra monsters in {}", this.extraMonsters.size(), System.currentTimeMillis() / 1000 - time);

        placeExtraMonsters();
    }

    private void placeExtraMonsters() {
        long time = System.currentTimeMillis() / 1000;

        this.extraMonsters.values().forEach(extraMonster -> extraMonster.getSubArea().forEach(subAreaId -> {
            byte random = (byte) Utils.random(0, 100);

            if (random <= extraMonster.getChance()) {
                SubArea subArea = this.subArea.get(subAreaId);
                subArea.getGameMap().get(new Random().nextInt(subArea.getGameMap().size())).setExtraMonster(extraMonster);
            }

        }));
        log.debug("Successfully places extra monsters in {}", System.currentTimeMillis() / 1000 - time);
    }

    private void loadExperiences() {
        long time = System.currentTimeMillis() / 1000;

        apply(get(EXPERIENCE).getElementsByTagName("experience"), element -> this.experiences.put(element.getAttribute("level").toShort(), new Experience(element)));

        this.experiences.keySet().forEach(i -> {
            Experience experience = this.experiences.get(i);
            experience.setNext(this.experiences.get((short) (i + 1)));
        });

        log.debug("Successfully load {} experiences data in {}", this.experiences.size(), System.currentTimeMillis() / 1000 - time);
    }

    private void loadNpcTemplates() {
        loadNpcData();
        long time = System.currentTimeMillis() / 1000;


        log.debug("Successfully load {} npc templates in {}", apply(get(NPC_TEMPLATE).getElementsByTagName("NpcTemplate"), element ->
                this.npcTemplates.put(element.getAttribute("id").toInt(), new NpcTemplate(element))), System.currentTimeMillis() / 1000 - time);
    }

    private void loadNpcData() {
        long time = System.currentTimeMillis() / 1000;

        log.debug("Successfully load {} npc answers in {}", apply(get(NPC_ANSWER).getElementsByTagName("NpcAnswer"), element ->
                this.npcAnswers.put(element.getAttribute("id").toShort(), new NpcAnswer(element))), System.currentTimeMillis() / 1000 - time);

        time = System.currentTimeMillis() / 1000;

        log.debug("Successfully load {} npc questions in {}", apply(get(NPC_QUESTION).getElementsByTagName("NpcQuestion"), element ->
                this.npcQuestions.put(element.getAttribute("id").toShort(), new NpcQuestion(element, this.npcAnswers))), System.currentTimeMillis() / 1000 - time);
    }

    private void loadMonsterTemplates() {
        long time = System.currentTimeMillis() / 1000;
        log.debug("Successfully load {} monster templates in {}", apply(get(MONSTER_TEMPLATE).getElementsByTagName("MonsterTemplate"),
                element -> this.monsterTemplates.put(element.getAttribute("id").toInt(), new MonsterTemplate(element))), System.currentTimeMillis() / 1000 - time);
    }

    private void loadItemTemplates() {
        long time = System.currentTimeMillis() / 1000;

        apply(get(ITEM_TEMPLATE).getElementsByTagName("item"), element -> {
            ItemTemplate template = new ItemTemplate(element);
            this.itemTemplates.put(template.getId(), template);
        });


        apply(get(ITEM_ACTION).getElementsByTagName("action"), element -> {
            String[] types = element.getElementByTagName("type").toString().split(";");
            String[] parameters = element.getElementByTagName("parameter").toString().split("\\|");

            for (byte i = 0; i < types.length; i++) {
                ItemAction action = ItemAction.get(Byte.parseByte(types[i]));
                if (action != null)
                    getItemTemplate(element.getAttribute("template").toShort()).addAction(action, parameters[i]);
            }
        });

        log.debug("Successfully load {} item templates in {}", this.itemTemplates.size(), System.currentTimeMillis() / 1000 - time);

        loadPanoplyTemplates();
    }

    private void loadPanoplyTemplates() {
        long time = System.currentTimeMillis() / 1000;

        apply(get(PANOPLY_TEMPLATE).getElementsByTagName("Panoply"), element -> {
            Map<Short, ItemTemplate> templates = new HashMap<>();
            for (String item : element.getElementByTagName("items").toString().split(",")) {
                short template = Short.parseShort(item);
                templates.put(template, this.itemTemplates.get(template));
            }
            this.panoply.put(element.getAttribute("id").toShort(), new Panoply(element, templates));
        });

        log.debug("Successfully load {} panoply in {}", this.panoply.size(), System.currentTimeMillis() / 1000 - time);
    }

    private void startScheduledAction() {
        this.scheduler.scheduleWithFixedDelay(() -> {
            placeExtraMonsters();
            this.gameMapRepository.getInitialized().forEach(gameMap -> worker.execute(gameMap::refreshMonsters));
            log.debug("Successfully refresh monsters");
        }, 1, 1, TimeUnit.DAYS);

        log.debug("Successfully start scheduled action");
    }

    @Override
    public void start() {
        this.itemIdentityGenerator = new AtomicInteger(database.getNextId(ITEMS, ITEMS.ID));
        new FastLoader(this::loadNpcTemplates, this::loadItemTemplates, this::loadMonsterTemplates, this::loadExperiences, this::loadSpells, this::loadGameMaps).launch();
        startScheduledAction();
    }

    public int apply(NodeList list, Performer performer) {
        IntStream.range(0, list.getLength()).forEach(i -> performer.accept(new XMLElement((Element) list.item(i))));
        return list.getLength();
    }


    @Override
    public void stop() {
        playerRepository.save();
    }

    private Document get(String path) {
        try {
            return (documentBuilderFactory.newDocumentBuilder().parse(getClass().getClassLoader().getResourceAsStream("data/" + path)));
        } catch (Exception e) {
            throw new NullPointerException("File " + path + " was not found");
        }
    }

    public GameMap getMap(int id) {
        return gameMapRepository.get(id);
    }

    public GameMap getMapByPosition(String position) {
        return this.gameMapRepository.getByPosition(position);
    }

    public int getNextItemId() {
        return this.itemIdentityGenerator.getAndIncrement();
    }
}
