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
import org.graviton.database.repository.*;
import org.graviton.game.action.fight.AbstractFightAction;
import org.graviton.game.action.item.ItemAction;
import org.graviton.game.area.Area;
import org.graviton.game.area.SubArea;
import org.graviton.game.command.api.AbstractCommand;
import org.graviton.game.creature.monster.MonsterTemplate;
import org.graviton.game.creature.monster.extra.ExtraMonster;
import org.graviton.game.creature.npc.NpcAnswer;
import org.graviton.game.creature.npc.NpcQuestion;
import org.graviton.game.creature.npc.NpcTemplate;
import org.graviton.game.drop.Drop;
import org.graviton.game.experience.Experience;
import org.graviton.game.house.House;
import org.graviton.game.house.HouseTemplate;
import org.graviton.game.items.Panoply;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.object.InteractiveObjectTemplate;
import org.graviton.game.spell.SpellTemplate;
import org.graviton.utils.Utils;
import org.graviton.xml.Performer;
import org.graviton.xml.XMLElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import static org.graviton.database.jooq.game.tables.HousesData.HOUSES_DATA;
import static org.graviton.database.jooq.game.tables.Items.ITEMS;


/**
 * Created by Botan on 11/11/2016 : 22:42
 */
@Slf4j
@Data
public class EntityFactory extends EntityData implements Manageable {
    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(Integer.MAX_VALUE);
    private final ExecutorService worker = Executors.newCachedThreadPool();

    private AtomicInteger itemIdentityGenerator;
    private GameDatabase database;

    @Inject
    private GameMapRepository gameMapRepository;

    @Inject
    private PlayerRepository playerRepository;

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private CommandRepository commandRepository;

    @Inject
    private ArtificialIntelligenceRepository intelligenceRepository;

    @Inject
    private GuildRepository guildRepository;

    @Inject
    public EntityFactory(Program program, @Named("database.game") AbstractDatabase database) {
        program.register(this);
        this.database = (GameDatabase) database;
    }

    private void loadFightActions() {
        apply(get(FIGHT_ACTION).getElementsByTagName("fightAction"), element -> this.fightAction.put(element.getAttribute("map").toInt(), new AbstractFightAction(element)));
        log.debug("Successfully load {} fight action", this.fightAction.size());
    }

    private void loadHouses() {
        apply(get(HOUSES).getElementsByTagName("house"), element -> this.houses.put(element.getAttribute("id").toShort(), new HouseTemplate(element)));
        log.debug("Successfully load {} houses", this.houses.size());
    }

    private void loadGuilds() {
        log.debug("Successfully load {} guilds", guildRepository.load());
    }

    private void loadCommands() {
        log.debug("Successfully load {} commands", commandRepository.load());
    }

    private void loadIntelligence() {
        log.debug("Successfully load {} artificial intelligence", intelligenceRepository.load());
    }

    private void loadSpells() {
        apply(get(SPELLS).getElementsByTagName("spell"), element -> this.spells.put(element.getAttribute("id").toShort(), new SpellTemplate(element)));
        log.debug("Successfully load {} spells", this.spells.size());

        loadGuilds();
    }

    private void loadGameMaps() {
        apply(get(INTERACTIVE_OBJECT).getElementsByTagName("InteractiveObject"), (element -> this.interactiveObjects.put(element.getAttribute("id").toInt(), new InteractiveObjectTemplate(element))));
        log.debug("Successfully load {} interactive object", this.interactiveObjects.size());

        apply(get(AREA).getElementsByTagName("Area"), (element -> this.area.put(element.getAttribute("id").toShort(), new Area(element))));
        log.debug("Successfully load {} area", this.area.size());

        apply(get(SUBAREA).getElementsByTagName("SubArea"), (element -> this.subArea.put(element.getAttribute("id").toShort(), new SubArea(element, this.area.get(element.getElementByTagName("area").toShort())))));
        log.debug("Successfully load {} sub area", this.subArea.size());

        log.debug("Successfully load {} game map", gameMapRepository.load(get(MAPS)));
        log.debug("Successfully register {} npc", gameMapRepository.loadNpc(get(NPCS)));
        log.debug("Successfully register {} houses", gameMapRepository.loadHouses());
        log.debug("Successfully register {} zaaps", gameMapRepository.loadZaaps(get(ZAAPS)));

        loadExtraMonsters();
    }

    private void loadExtraMonsters() {
        apply(get(EXTRA_MONSTERS).getElementsByTagName("ExtraMonster"), element -> {
            int id = element.getAttribute("id").toInt();
            ExtraMonster extraMonster = new ExtraMonster(getMonsterTemplate(id), element.getElementByTagName("chance").toByte());

            for (String subArea : element.getElementByTagName("subarea").toString().split(","))
                extraMonster.registerSubArea(Short.parseShort(subArea));

            extraMonsters.put(id, extraMonster);
        });

        log.debug("Successfully load {} extra monsters", this.extraMonsters.size());

        placeExtraMonsters();
    }

    private void placeExtraMonsters() {
        this.extraMonsters.values().forEach(extraMonster -> extraMonster.getSubArea().forEach(subAreaId -> {
            byte random = (byte) Utils.random(0, 100);

            if (random <= extraMonster.getChance()) {
                SubArea subArea = this.subArea.get(subAreaId);
                subArea.getGameMaps().get(new Random().nextInt(subArea.getGameMaps().size())).setExtraMonster(extraMonster);
            }

        }));
        log.debug("Successfully places extra monsters");
    }

    private void loadExperiences() {
        apply(get(EXPERIENCE).getElementsByTagName("experience"), element -> this.experiences.put(element.getAttribute("level").toShort(), new Experience(element)));

        this.experiences.keySet().forEach(i -> {
            Experience experience = this.experiences.get(i);
            experience.setNext(this.experiences.get((short) (i + 1)));
        });

        log.debug("Successfully load {} experiences data", this.experiences.size());
    }

    private void loadNpcTemplates() {
        loadNpcData();

        log.debug("Successfully load {} npc templates", apply(get(NPC_TEMPLATE).getElementsByTagName("NpcTemplate"), element ->
                this.npcTemplates.put(element.getAttribute("id").toInt(), new NpcTemplate(element))));
    }

    private void loadNpcData() {
        log.debug("Successfully load {} npc answers", apply(get(NPC_ANSWER).getElementsByTagName("NpcAnswer"), element ->
                this.npcAnswers.add(new NpcAnswer(element))));

        log.debug("Successfully load {} npc questions", apply(get(NPC_QUESTION).getElementsByTagName("NpcQuestion"), element ->
                this.npcQuestions.put(element.getAttribute("id").toShort(), new NpcQuestion(element, this.npcAnswers))));
    }

    private void loadMonsterTemplates() {
        loadSpells();

        log.debug("Successfully load {} monster templates", apply(get(MONSTER_TEMPLATE).getElementsByTagName("MonsterTemplate"),
                element -> this.monsterTemplates.put(element.getAttribute("id").toInt(), new MonsterTemplate(element, this))));

        AtomicInteger appliquedDrop = new AtomicInteger(0);
        short drops = (short) apply(get(DROPS).getElementsByTagName("Drop"), element -> {
            Drop drop = new Drop(element);
            if (monsterTemplates.containsKey(drop.getMonster())) {
                getMonsterTemplate(drop.getMonster()).getDrops().add(drop);
                appliquedDrop.incrementAndGet();
            } else
                this.drops.add(drop);
        });

        log.debug("Successfully load {} drops", drops);
        log.debug("Successfully appliqued {} drops", appliquedDrop);
        log.debug("Successfully add {} free drops", drops - appliquedDrop.intValue());
        log.debug("Successfully register {} monsters groups", gameMapRepository.loadMonsterGroups(get(MONSTER_GROUP)));
    }

    private void loadItemTemplates() {
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

        log.debug("Successfully load {} item templates", this.itemTemplates.size());

        loadPanoplyTemplates();
    }

    private void loadPanoplyTemplates() {
        apply(get(PANOPLY_TEMPLATE).getElementsByTagName("Panoply"), element -> {
            Map<Short, ItemTemplate> templates = new HashMap<>();
            for (String item : element.getElementByTagName("items").toString().split(",")) {
                short template = Short.parseShort(item);
                templates.put(template, this.itemTemplates.get(template));
            }
            this.panoply.put(element.getAttribute("id").toShort(), new Panoply(element, templates));
        });

        log.debug("Successfully load {} panoply", this.panoply.size());
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
      /** StringBuilder builder = new StringBuilder();

        database.getResult(Zaapi.ZAAPI).forEach(record -> {
            builder.append("<zaapi map=\"" + record.get(Zaapi.ZAAPI.MAPID) + "\" alignment=\"" + record.get((Zaapi.ZAAPI.ALIGN)  + "\" /> \n"));
        });

        FileWriter fw= null;
        File file =null;
        try {
            file=new File("WriteFile.txt");
            if(!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file);
            fw.write(builder.toString());
            fw.flush();
            fw.close();
            System.out.println("File written Succesfully");
        } catch (IOException e) {
            e.printStackTrace();
        } **/

        this.itemIdentityGenerator = new AtomicInteger(database.getNextId(ITEMS, ITEMS.ID));
        log.debug("Successfully initialize item identity generator [{}]", itemIdentityGenerator.get());
        new FastLoader(this::loadFightActions,this::loadHouses, this::loadNpcTemplates, this::loadItemTemplates, this::loadMonsterTemplates, this::loadExperiences, this::loadGameMaps,
                this::loadCommands, this::loadIntelligence).launch();
        startScheduledAction();
    }

    @Override
    public void stop() {
        this.playerRepository.save();
    }

    public int apply(NodeList list, Performer performer) {
        IntStream.range(0, list.getLength()).forEach(i -> performer.accept(new XMLElement((Element) list.item(i))));
        return list.getLength();
    }


    private Document get(String path) {
        try {
            return (documentBuilderFactory.newDocumentBuilder().parse(getClass().getClassLoader().getResourceAsStream("data/" + path)));
        } catch (Exception e) {
            throw new NullPointerException("File " + path + " was not found");
        }
    }

    public GameMap getMap(Object value) {
        return gameMapRepository.find(value);
    }


    public int getNextItemId() {
        return this.itemIdentityGenerator.getAndIncrement();
    }


    public AbstractCommand getCommand(String name) {
        return commandRepository.get(name);
    }

    public void updateHouse(House house) {
        database.update(HOUSES_DATA).set(HOUSES_DATA.OWNER, house.getOwner()).set(HOUSES_DATA.SALE, house.getPrice()).set(HOUSES_DATA.KEY, house.getKey())
                .where(HOUSES_DATA.ID.equal(house.getTemplate().getId())).execute();
    }
}
