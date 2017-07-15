package org.graviton.database.entity;

import com.google.inject.Inject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.graviton.annotation.Scheduler;
import org.graviton.annotation.Worker;
import org.graviton.api.Manageable;
import org.graviton.core.Program;
import org.graviton.core.loader.FastLoader;
import org.graviton.database.Database;
import org.graviton.database.api.GameDatabase;
import org.graviton.database.repository.*;
import org.graviton.game.action.Action;
import org.graviton.game.action.fight.AbstractFightAction;
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
import org.graviton.game.job.JobTemplate;
import org.graviton.game.job.craft.CraftData;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.object.InteractiveObjectTemplate;
import org.graviton.game.quest.QuestGoal;
import org.graviton.game.quest.QuestTemplate;
import org.graviton.game.quest.stape.QuestStep;
import org.graviton.game.sellpoint.SellPointItem;
import org.graviton.game.spell.SpellTemplate;
import org.graviton.script.ScriptProcessor;
import org.graviton.utils.Utils;
import org.graviton.xml.Performer;
import org.graviton.xml.XMLElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.graviton.constant.XMLPath.*;
import static org.graviton.database.jooq.game.tables.HousesData.HOUSES_DATA;

import static org.graviton.database.jooq.game.tables.Items.ITEMS;
import static org.graviton.database.jooq.game.tables.SellpointItems.SELLPOINT_ITEMS;


/**
 * Created by Botan on 11/11/2016 : 22:42
 */
@Slf4j
@Data
public class EntityFactory extends EntityData implements Manageable {
    private @Inject @Scheduler ScheduledThreadPoolExecutor scheduler;
    private @Inject @Worker ExecutorService worker;

    private Database database;

    @Inject private GameMapRepository gameMapRepository;

    @Inject private PlayerRepository playerRepository;

    @Inject private AccountRepository accountRepository;

    @Inject private CommandRepository commandRepository;

    @Inject private ArtificialIntelligenceRepository intelligenceRepository;

    @Inject private GuildRepository guildRepository;

    @Inject private ActionRepository actionRepository;

    @Inject private ScriptProcessor scriptProcessor;

    private final AtomicInteger sellPointIdentity = new AtomicInteger(0);
    private final AtomicInteger itemIdentity = new AtomicInteger(0);


    @Inject
    public EntityFactory(Program program, @GameDatabase Database database) {
        program.register(this);
        this.database = database;
    }

    private void loadQuests() {
        apply(get(QUEST_GOALS).getElementsByTagName("QuestGoal"), element -> this.questGoals.put(element.getAttribute("id").toShort(), new QuestGoal(element)));
        log.debug("{} quest goals loaded", this.questGoals.size());

        apply(get(QUEST_STEPS).getElementsByTagName("QuestStep"), element -> this.questSteps.put(element.getAttribute("id").toShort(), new QuestStep(element, this)));
        log.debug("{} quest steps loaded", this.questSteps.size());

        apply(get(QUESTS).getElementsByTagName("Quest"), element -> this.quests.put(element.getAttribute("id").toShort(), new QuestTemplate(element, this)));
        log.debug("{} quest loaded", this.quests.size());
    }

    private void loadJobs() {
        apply(get(CRAFTS).getElementsByTagName("craft"), element -> this.crafts.put(element.getAttribute("item").toShort(), new CraftData(element)));
        log.debug("{} crafts loaded", this.crafts.size());

        apply(get(JOBS).getElementsByTagName("job"), element -> this.jobs.put(element.getAttribute("id").toShort(), new JobTemplate(element, this)));
        log.debug("{} jobs loaded", this.jobs.size());

        scriptProcessor.importElement(this, "entityFactory");
        scriptProcessor.loadPath("scripts/jobs");
    }

    private void loadGameActions() {
        log.debug("{} game actions loaded", actionRepository.initialize());
    }

    private void loadFightActions() {
        apply(get(FIGHT_ACTION).getElementsByTagName("fightAction"), element -> this.fightAction.put(element.getAttribute("map").toInt(), new AbstractFightAction(element, this)));
        log.debug("{} fight action loaded", this.fightAction.size());
    }

    private void loadHouses() {
        apply(get(HOUSES).getElementsByTagName("house"), element -> this.houses.put(element.getAttribute("id").toShort(), new HouseTemplate(element)));
        log.debug("{} houses loaded", this.houses.size());
    }

    private void loadGuilds() {
        log.debug("{} guilds loaded", guildRepository.load());
    }

    private void loadCommands() {
        log.debug("{} commands loaded", commandRepository.load());
    }

    private void loadIntelligence() {
        log.debug("{} artificial intelligence loaded", intelligenceRepository.load());
    }

    private void loadSpells() {
        apply(get(SPELLS).getElementsByTagName("spell"), element -> this.spells.put(element.getAttribute("id").toShort(), new SpellTemplate(element)));
        log.debug("{} spells loaded", this.spells.size());
        loadGuilds();
    }

    private void loadGameMaps() {
        apply(get(INTERACTIVE_OBJECT).getElementsByTagName("InteractiveObject"), (element -> this.interactiveObjects.put(element.getAttribute("id").toInt(), new InteractiveObjectTemplate(element))));
        log.debug("{} interactive object loaded", this.interactiveObjects.size());

        apply(get(AREA).getElementsByTagName("Area"), (element -> this.area.put(element.getAttribute("id").toShort(), new Area(element))));
        log.debug("{} area loaded", this.area.size());

        apply(get(SUBAREA).getElementsByTagName("SubArea"), (element -> this.subArea.put(element.getAttribute("id").toShort(), new SubArea(element, this.area.get(element.getElementByTagName("area").toShort())))));
        log.debug("{} sub area loaded", this.subArea.size());

        log.debug("{} game map loaded", gameMapRepository.load(get(MAPS)));
        log.debug("{} npc stored", gameMapRepository.loadNpc(get(NPCS)));
        log.debug("{} houses stored", gameMapRepository.loadHouses());
        log.debug("{} trunks loaded", gameMapRepository.loadTrunks());
        log.debug("{} zaaps loaded", gameMapRepository.loadZaaps(get(ZAAPS)));
        log.debug("{} zaapis loaded", gameMapRepository.loadZaapis(get(ZAAPIS)));
        log.debug("{} doors loaded", gameMapRepository.loadDoors(get(INTERACTIVE_DOOR)));
        log.debug("{} mount-park loaded", gameMapRepository.loadMountPark());
        log.debug("{} sell points loaded", gameMapRepository.loadSellPoint(get(SELL_POINTS)));
        log.debug("{} sell points item loaded", database.getResult(SELLPOINT_ITEMS).stream()
                .filter(record -> gameMapRepository.get(record.get(SELLPOINT_ITEMS.MAP)).getSellPoint().add(new SellPointItem(record, playerRepository), true)).count());


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

        log.debug("{} extra monsters loaded", this.extraMonsters.size());

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
        log.debug("extra monsters placed");
    }

    private void loadExperiences() {
        apply(get(EXPERIENCE).getElementsByTagName("experience"), element -> this.experiences.put(element.getAttribute("level").toShort(), new Experience(element)));

        this.experiences.keySet().forEach(i -> {
            Experience experience = this.experiences.get(i);
            experience.setNext(this.experiences.get((short) (i + 1)));
        });

        log.debug("{} experiences data loaded", this.experiences.size());
    }

    private void loadNpcTemplates() {
        loadNpcData();

        log.debug("{} npc templates loaded", apply(get(NPC_TEMPLATE).getElementsByTagName("NpcTemplate"), element ->
                this.npcTemplates.put(element.getAttribute("id").toInt(), new NpcTemplate(element))));
    }

    public void loadNpcData() {
        log.debug("{} npc answers loaded", apply(get(NPC_ANSWER).getElementsByTagName("NpcAnswer"), element ->
                this.npcAnswers.add(new NpcAnswer(element, this))));

        log.debug("{} npc questions loaded", apply(get(NPC_QUESTION).getElementsByTagName("NpcQuestion"), element ->
                this.npcQuestions.put(element.getAttribute("id").toShort(), new NpcQuestion(element, this.npcAnswers))));
    }

    private void loadMonsterTemplates() {
        loadSpells();

        log.debug("{} monster templates loaded", apply(get(MONSTER_TEMPLATE).getElementsByTagName("MonsterTemplate"),
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

        log.debug("{} drops loaded", drops);
        log.debug("{} drops appliqued", appliquedDrop);
        log.debug("{} free drops stored", drops - appliquedDrop.intValue());
        log.debug("{} monsters groups stored", gameMapRepository.loadMonsterGroups(get(MONSTER_GROUP)));
    }

    private void loadItemTemplates() {
        apply(get(ITEM_TEMPLATE).getElementsByTagName("item"), element -> {
            ItemTemplate template = new ItemTemplate(element);
            this.itemTemplates.put(template.getId(), template);
        });


        apply(get(ITEM_ACTION).getElementsByTagName("action"), element -> {
            String[] types = element.getElementByTagName("type").toString().split(";");
            String[] parameters = element.getElementByTagName("parameter").toString().split("\\|");

            ItemTemplate template = getItemTemplate(element.getAttribute("template").toShort());

            for (byte i = 0; i < types.length; i++) {
                Action action = actionRepository.create(Short.parseShort(types[i]));
                if (action != null && template != null)
                    template.addAction(action, parameters[i]);
            }
        });

        log.debug("{} item templates loaded", this.itemTemplates.size());

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

        log.debug("{} panoply loaded", this.panoply.size());
    }

    private void startScheduledAction() {
        this.scheduler.scheduleWithFixedDelay(() -> {
            placeExtraMonsters();
            this.gameMapRepository.getInitialized().forEach(gameMap -> worker.execute(gameMap::refreshMonsters));
            log.debug("refresh monsters : done");
        }, 1, 1, TimeUnit.DAYS);

        log.debug("scheduled action : started");
    }

    @Override
    public void start() {
        this.loadGameActions();

        new FastLoader(this::loadFightActions, this::loadHouses, this::loadNpcTemplates, this::loadItemTemplates, this::loadMonsterTemplates, this::loadExperiences, this::loadGameMaps,
                this::loadCommands, this::loadIntelligence, this::loadJobs, this::loadQuests).launch();
        startScheduledAction();

        this.itemIdentity.set(database.getNextId(ITEMS, ITEMS.ID));
    }

    @Override
    public void stop() {
        this.playerRepository.save();
    }

    @Override public byte index() {
        return 2;
    }

    public int apply(NodeList list, Performer performer) {
        IntStream.range(0, list.getLength()).forEach(i -> performer.accept(new XMLElement((Element) list.item(i))));
        return list.getLength();
    }


    private Document get(String path) {
        try {
            return (documentBuilderFactory.newDocumentBuilder().parse("data/" + path));
        } catch (Exception e) {
            throw new NullPointerException("File " + path + " was not found");
        }
    }

    public GameMap getMap(Object value) {
        return gameMapRepository.find(value);
    }


    public int getNextItemId() {
        return itemIdentity.incrementAndGet();
    }


    public AbstractCommand getCommand(String name) {
        return commandRepository.get(name);
    }

    public void updateHouse(House house) {
        database.update(HOUSES_DATA).set(HOUSES_DATA.OWNER, house.getOwner()).set(HOUSES_DATA.SALE, house.getPrice()).set(HOUSES_DATA.KEY, house.getKey())
                .where(HOUSES_DATA.ID.equal(house.getTemplate().getId())).execute();
    }

    public int nextSellPointLine() {
        return this.sellPointIdentity.incrementAndGet();
    }

    public void saveSellPointItem(SellPointItem sellPointItem, int gameMap) {
        database.getDslContext().insertInto(SELLPOINT_ITEMS, SELLPOINT_ITEMS.ID, SELLPOINT_ITEMS.MAP, SELLPOINT_ITEMS.OWNER, SELLPOINT_ITEMS.PRICE,SELLPOINT_ITEMS.DATE)
                .values(sellPointItem.getItem().getId(), gameMap, sellPointItem.getOwner(), sellPointItem.getPrice(), new Date().getTime()).execute();
    }

    public void removeSellPointItem(int id) {
        database.getDslContext().delete(SELLPOINT_ITEMS).where(SELLPOINT_ITEMS.ID.equal(id)).execute();
    }

}
