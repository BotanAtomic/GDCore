package org.graviton.game.maps;

import javafx.util.Pair;
import lombok.Data;
import org.graviton.game.creature.Creature;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.alignment.Alignment;
import org.graviton.game.creature.merchant.Merchant;
import org.graviton.game.creature.monster.Monster;
import org.graviton.game.creature.monster.MonsterGroup;
import org.graviton.game.creature.monster.MonsterTemplate;
import org.graviton.game.creature.monster.extra.ExtraMonster;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.FightFactory;
import org.graviton.game.sellpoint.SellPoint;
import org.graviton.game.house.House;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.cell.Trigger;
import org.graviton.game.maps.fight.FightMap;
import org.graviton.game.maps.utils.CellLoader;
import org.graviton.game.mountpark.MountPark;
import org.graviton.game.trunk.type.Trunk;
import org.graviton.game.zaap.Zaap;
import org.graviton.game.zaap.Zaapi;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.utils.Cells;
import org.graviton.utils.Utils;
import org.graviton.xml.Attribute;
import org.graviton.xml.XMLElement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Maps.newConcurrentMap;
import static java.util.Collections.synchronizedList;
import static org.graviton.utils.Utils.random;

/**
 * Created by Botan on 12/11/2016 : 17:55
 */
@Data
public class GameMap implements AbstractMap {
    private final EntityFactory entityFactory;

    private final List<Creature> toRegister = new ArrayList<>();

    private final int id;
    private final String date, key, data, places, position;
    private final byte width, height;
    private final Map<Integer, Creature> creatures = newConcurrentMap();
    private final String descriptionPacket;
    private final AtomicInteger idGenerator = new AtomicInteger(-1000);

    private Map<Short, Cell> cells;
    private Map<Short, Trigger> triggers;

    private List<Pair<Integer, Short>> possibleGroups;
    private byte minimumGroupSize, maximumGroupSize, fixedGroupSize;
    private byte numberOfGroup;

    private FightFactory fightFactory;

    private ExtraMonster extraMonster;

    private boolean initialized = false;
    private String monsters, triggersData;

    private Map<Short, House> houses;
    private Map<Short, Trunk> trunks;

    private MountPark mountPark;

    private Zaap zaap;
    private Zaapi zaapi;

    private SellPoint sellPoint;

    private byte[] restriction = {0, 0, 0, 0, 0, 0, 0};

    public GameMap(int id, XMLElement element, EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
        this.id = id;

        this.date = element.getElementByTagName("data", "date").toString();
        this.key = element.getElementByTagName("data", "key").toString();
        this.data = element.getElementByTagName("data").toString();
        this.places = element.getElementByTagName("places").toString();
        this.position = Utils.parsePosition(element.getElementByTagName("position").toString());

        this.width = element.getElementByTagName("size", "width").toByte();
        this.height = element.getElementByTagName("size", "height").toByte();

        this.triggersData = element.getElementByTagName("triggers").toString();

        this.minimumGroupSize = element.getElementByTagName("monsters", "min").toByte();
        this.maximumGroupSize = element.getElementByTagName("monsters", "max").toByte();
        this.fixedGroupSize = element.getElementByTagName("monsters", "fix").toByte();

        this.numberOfGroup = element.getElementByTagName("monsters", "count").toByte();

        this.descriptionPacket = GamePacketFormatter.mapDataMessage(this.id, this.date, this.key);
        this.monsters = element.getElementByTagName("monsters").toString();

        Attribute restriction = element.getSecureElementByTag("restriction");
        if(restriction != null && !restriction.toString().isEmpty()) {
            String[] data = restriction.toString().split(";");
            IntStream.range(0,7).forEach(i -> this.restriction[i] = Byte.parseByte(data[i]));
        }
    }


    public short getX() {
        return Short.parseShort(position.split(",")[0]);
    }

    public short getY() {
        return Short.parseShort(position.split(",")[1]);
    }

    public synchronized GameMap initialize() {
        if (initialized)
            return this;

        this.cells = CellLoader.parse(this, this.data, this.entityFactory, false);
        this.triggers = (CellLoader.parseTrigger(triggersData));
        this.toRegister.forEach(creature -> register(creature, false));

        if (!(this.possibleGroups = synchronizedList(initializeMonsters(monsters))).isEmpty())
            this.generateMonsters(false);

        this.fightFactory = new FightFactory(this);
        this.initialized = true;

        if (this.mountPark != null)
            this.mountPark.initialize();

        entityFactory.getPlayerRepository().loadMerchant(this.id).forEach(merchant -> register(merchant, false));

        return this;
    }

    private List<Pair<Integer, Short>> initializeMonsters(String monstersData) {
        if (monstersData.isEmpty())
            return new ArrayList<>();

        List<Pair<Integer, Short>> values = new ArrayList<>();

        for (String data : monstersData.split("\\|")) {
            if (!data.isEmpty())
                values.add(new Pair<>(Integer.parseInt(data.split(",")[0]), Short.parseShort(data.split(",")[1])));
        }
        return values;
    }

    private void generateMonsters(boolean send) {
        IntStream.range(0, numberOfGroup).forEach(i -> register(randomMonsterGroup(), send));
    }

    public Collection<MonsterGroup> monsters() {
        return this.creatures.values().stream().filter(creature -> creature instanceof MonsterGroup).map(creature -> (MonsterGroup) creature).collect(Collectors.toList());
    }

    public MonsterGroup searchMonsterGroupByPath(Alignment alignment, short currentCell) {
        return monsters().stream().filter(monsterGroup -> monsterGroup.aggressionDistance(alignment.getId()) > 0).
                filter(monsterGroup -> Cells.distanceBetween(this.width, monsterGroup.getLocation().getCell().getId(), currentCell) < monsterGroup.aggressionDistance(alignment.getId())).findFirst().orElse(null);
    }

    public MonsterGroup randomMonsterGroup() {
        Random random = new Random();
        Collection<Monster> monsters = new ArrayList<>();
        byte groupSize = fixedGroupSize > 0 ? fixedGroupSize : (byte) random(minimumGroupSize, maximumGroupSize);

        IntStream.range(0, groupSize).forEach(inc -> {
            if (this.extraMonster != null && this.extraMonster.getTemplate() != null) {
                monsters.add(this.extraMonster.getTemplate().getRandom());
                this.extraMonster = null;
            } else {
                Pair<Integer, Short> randomPair = this.possibleGroups.get(random.nextInt(this.possibleGroups.size()));
                MonsterTemplate template = entityFactory.getMonsterTemplate(randomPair.getKey());
                Monster monster;

                if (template == null || (monster = template.getByLevel(randomPair.getValue())) == null)
                    return;

                monsters.add(monster);
            }
        });
        return new MonsterGroup(getNextId(), this, getRandomCell(), monsters);
    }

    public String interactiveObjectData() {
        StringBuilder builder = new StringBuilder();
        this.cells.values().stream().filter(cell -> Objects.nonNull(cell.getInteractiveObject())).forEach(cell ->
                builder.append(cell.getInteractiveObject().getData())
                        .append("#"));
        return builder.toString();
    }

    @Override
    public String buildData() {
        StringBuilder packet = new StringBuilder();
        creatures.values().forEach(creature -> packet.append(GamePacketFormatter.showCreatureMessage(creature.getGm())).append('\n'));
        return packet.toString();
    }


    public void addFuture(Creature creature) {
        this.toRegister.add(creature);
    }

    public void register(Creature creature, boolean send) {
        this.creatures.put(creature.getId(), creature);
        creature.getLocation().getCell().getCreatures().add(creature.getId());

        if (send)
            send(GamePacketFormatter.showCreatureMessage(creature.getGm()));
    }

    public int getNextId() {
        return idGenerator.incrementAndGet();
    }

    public void enterAfterFight(Creature creature) {
        creature.send(this.descriptionPacket);
        creature.getLocation().getCell().getCreatures().add(creature.getId());
        enter(creature);
    }

    public void enter(Creature creature) {
        if(this.creatures.containsKey(creature.getId()))
            out(this.creatures.get(creature.getId()));

        send(GamePacketFormatter.showCreatureMessage(creature.getGm()));
        this.creatures.put(creature.getId(), creature);
    }

    public void load(Creature creature) {
        creature.send(this.descriptionPacket);
        creature.send(GamePacketFormatter.creatureChangeMapMessage(creature.getId()));
        creature.send(GamePacketFormatter.mapLoadedSuccessfullyMessage());
        creature.getLocation().getCell().getCreatures().add(creature.getId());
        creature.getLocation().setMap(this);
    }

    public void loadOnlyData(Creature creature, GameClient client) {
        client.send(this.descriptionPacket);
        client.send(GamePacketFormatter.creatureChangeMapMessage(creature.getId()));
        client.send(GamePacketFormatter.mapLoadedSuccessfullyMessage());
    }

    public void loadAndEnter(Creature creature) {
        load(creature);
        enter(creature);
    }

    @Override
    public void send(String data) {
        this.creatures.values().forEach(creature -> creature.send(data));
    }

    public void out(Creature creature) {
        creature.getLocation().getCell().getCreatures().remove(creature.getId());
        this.creatures.remove(creature.getId());
        send(GamePacketFormatter.hideCreatureMessage(creature.getId()));
    }

    public short getRandomCell() {
        return Utils.getRandomObject(this.cells.values().stream().filter(cell -> cell.isWalkable() &&
                cell.getCreatures().isEmpty()).collect(Collectors.toList())).getId();
    }

    public Creature getCreature(int id) {
        return this.creatures.get(id);
    }

    public void refreshCreature(Creature creature) {
        send(GamePacketFormatter.hideCreatureMessage(creature.getId()));
        send(GamePacketFormatter.showCreatureMessage(creature.getGm()));
    }

    public void refreshMonsters() {
        if (!this.possibleGroups.isEmpty()) {
            this.creatures.values().stream().filter(creature -> creature instanceof MonsterGroup).forEach(this::out);
            generateMonsters(true);
        }
    }

    public FightMap createFightMap(Fight fight) {
        return new FightMap(this, fight);
    }

    public boolean canPlaceCollector() {
        return this.creatures.values().stream().filter(creature -> creature instanceof Collector).count() == 0;
    }

    public void addHouse(House house) {
        if (this.houses == null)
            this.houses = new ConcurrentHashMap<>();
        this.houses.put(house.getTemplate().getGameCell(), house);
    }

    public void addTrunk(Trunk trunk, short cell) {
        if (this.trunks == null)
            this.trunks = new ConcurrentHashMap<>();
        this.trunks.put(cell, trunk);
    }

    public boolean restrictMerchant() {
        return restriction[0] == 1;
    }

    public boolean restrictCollector() {
        return restriction[1] == 1;
    }

    public boolean restrictPrism() {
        return restriction[2] == 1;
    }

    public boolean restrictTeleportation() {
        return restriction[3] == 1;
    }

    public boolean restrictDefy() {
        return restriction[4] == 1;
    }

    public boolean restrictAggression() {
        return restriction[5] == 1;
    }

    public boolean restrictCanal() {
        return restriction[6] == 1;
    }

    public int merchantCount() {
        return (int) this.creatures.values().stream().filter(creature -> creature instanceof Merchant).count();
    }

}
