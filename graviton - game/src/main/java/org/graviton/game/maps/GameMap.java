package org.graviton.game.maps;

import javafx.util.Pair;
import lombok.Data;
import org.graviton.api.Creature;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.creature.monster.Monster;
import org.graviton.game.creature.monster.MonsterGroup;
import org.graviton.game.creature.monster.MonsterTemplate;
import org.graviton.game.creature.monster.extra.ExtraMonster;
import org.graviton.game.fight.FightFactory;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.cell.Trigger;
import org.graviton.game.maps.utils.CellLoader;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.utils.Utils;
import org.graviton.xml.XMLElement;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Maps.newConcurrentMap;
import static java.util.Collections.synchronizedList;
import static org.graviton.utils.Utils.random;

/**
 * Created by Botan on 12/11/2016 : 17:55
 */
@Data
public class GameMap {
    private final EntityFactory entityFactory;

    private final int id;
    private final String date, key, data, places, position;
    private final byte width, height;
    private final Map<Short, Cell> cells;
    private final Map<Integer, Creature> creatures = newConcurrentMap();
    private final String descriptionPacket;
    private final AtomicInteger idGenerator = new AtomicInteger(-1000);
    private Map<Short, Trigger> triggers;
    private List<Pair<Integer, Short>> possibleGroups;
    private byte minimumGroupSize, maximumGroupSize, fixedGroupSize;
    private byte numberOfGroup;

    private FightFactory fightFactory;

    private ExtraMonster extraMonster;

    private boolean initialized = false;
    private String monsters, triggersData;

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

        this.cells = CellLoader.parse(this.data);
        this.triggersData = element.getElementByTagName("triggers").toString();

        this.minimumGroupSize = element.getElementByTagName("monsters", "min").toByte();
        this.maximumGroupSize = element.getElementByTagName("monsters", "max").toByte();
        this.fixedGroupSize = element.getElementByTagName("monsters", "fix").toByte();

        this.numberOfGroup = element.getElementByTagName("monsters", "count").toByte();

        this.descriptionPacket = GamePacketFormatter.mapDataMessage(this.id, this.date, this.key);
        this.monsters = element.getElementByTagName("monsters").toString();
    }

    private GameMap(GameMap gameMap) {
        this.entityFactory = null;

        this.id = gameMap.getId();

        this.date = gameMap.getDate();
        this.key = gameMap.getKey();
        this.data = gameMap.getData();
        this.places = gameMap.getPlaces();
        this.position = gameMap.getPosition();

        this.width = gameMap.getWidth();
        this.height = gameMap.getHeight();

        this.cells = (CellLoader.parse(data));

        this.minimumGroupSize = 0;
        this.maximumGroupSize = 0;
        this.fixedGroupSize = 0;

        this.numberOfGroup = 0;

        this.descriptionPacket = GamePacketFormatter.mapDataMessage(this.id, this.date, this.key);

    }

    public GameMap initialize() {
        if (initialized)
            return this;

        this.triggers = (CellLoader.parseTrigger(triggersData));

        if (!(this.possibleGroups = synchronizedList(initializeMonsters(monsters))).isEmpty())
            this.generateMonsters(false);

        this.fightFactory = new FightFactory(this);
        this.initialized = true;
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
        Random random = new Random();
        IntStream.range(0, numberOfGroup).forEach(i -> {
            Collection<Monster> monsters = new ArrayList<>();
            byte groupSize = fixedGroupSize > 0 ? fixedGroupSize : (byte) random(minimumGroupSize, maximumGroupSize);

            IntStream.range(0, groupSize).forEach(inc -> {
                if (this.extraMonster != null) {
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
            register(new MonsterGroup(getNextId(), this, getRandomCell(), monsters), send);
        });
    }

    private String buildData() {
        StringBuilder packet = new StringBuilder();
        creatures.values().forEach(creature -> packet.append(GamePacketFormatter.showCreatureMessage(creature.getGm())).append("\n"));
        return packet.toString();
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

    public void enter(Creature creature) {
        send(GamePacketFormatter.showCreatureMessage(creature.getGm()));

        this.creatures.put(creature.getId(), creature);
        creature.send(buildData());
    }

    public void load(Creature creature) {
        creature.send(this.descriptionPacket);
        creature.send(GamePacketFormatter.creatureChangeMapMessage(creature.getId()));
        creature.send(GamePacketFormatter.mapLoadedSuccessfullyMessage());
        creature.getLocation().setGameMap(this);
        creature.getLocation().getCell().getCreatures().add(creature.getId());
    }

    public void send(String data) {
        this.creatures.values().forEach(creature -> creature.send(data));
    }

    public void out(Creature creature) {
        creature.getLocation().getCell().getCreatures().remove(creature.getId());
        this.creatures.remove(creature.getId());
        send(GamePacketFormatter.hideCreatureMessage(creature.getId()));
    }

    public short getRandomCell() {
        List<Cell> freeCells = this.cells.values().stream().filter(cell -> cell.isWalkable() &&
                cell.getCreatures().isEmpty()).collect(Collectors.toList());
        return freeCells.get(random(0, freeCells.size() - 1)).getId();
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

    public GameMap copy() {
        return new GameMap(this);
    }

}
