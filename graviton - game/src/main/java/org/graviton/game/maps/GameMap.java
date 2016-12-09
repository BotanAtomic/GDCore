package org.graviton.game.maps;

import javafx.util.Pair;
import lombok.Data;
import org.graviton.api.Creature;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.creature.monster.Monster;
import org.graviton.game.creature.monster.MonsterGroup;
import org.graviton.game.creature.npc.Npc;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.cell.Trigger;
import org.graviton.game.maps.utils.CellLoader;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.jooq.Record;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Maps.newConcurrentMap;
import static java.util.Collections.synchronizedList;
import static org.graviton.database.jooq.game.tables.Maps.MAPS;
import static org.graviton.utils.Utils.random;

/**
 * Created by Botan on 12/11/2016 : 17:55
 */
@Data
public class GameMap {
    private final int id;
    private final String date, key, data, places;
    private final byte width, height;

    private final Map<Short, Trigger> triggers = newConcurrentMap();
    private final Map<Short, Cell> cells = newConcurrentMap();

    private final Map<Integer, Creature> creatures = newConcurrentMap();

    private final String descriptionPacket;

    private final AtomicInteger idGenerator = new AtomicInteger(-1000);
    private final List<Pair<Integer, Short>> possibleGroups;
    private byte minimumGroupSize, maximumGroupSize, fixedGroupSize;
    private byte numberOfGroup;

    public GameMap(Record record, EntityFactory entityFactory) {
        this.id = record.get(MAPS.ID);

        this.date = record.get(MAPS.DATE);
        this.key = record.get(MAPS.KEY);
        this.data = record.get(MAPS.MAPDATA);
        this.places = record.get(MAPS.PLACES);

        this.width = record.get(MAPS.WIDTH);
        this.height = record.get(MAPS.HEIGTH);

        this.cells.putAll(CellLoader.parse(data));
        this.triggers.putAll(CellLoader.parseTrigger(record.get(MAPS.TRIGGERS)));

        this.minimumGroupSize = record.get(MAPS.MINSIZE);
        this.maximumGroupSize = record.get(MAPS.MAXSIZE);
        this.fixedGroupSize = record.get(MAPS.FIXSIZE);

        this.numberOfGroup = record.get(MAPS.NUMGROUP);

        this.descriptionPacket = GamePacketFormatter.mapDataMessage(this.id, this.date, this.key);

        if (!(this.possibleGroups = synchronizedList(initializeMonsters(record.get(MAPS.MONSTERS)))).isEmpty())
            this.generateMonsters(entityFactory);
    }

    private List<Pair<Integer, Short>> initializeMonsters(String monstersData) {
        if (monstersData.isEmpty())
            return new ArrayList<>();

        List<Pair<Integer, Short>> values = new ArrayList<>();

        for (String data : monstersData.split("\\|"))
            values.add(new Pair<>(Integer.parseInt(data.split(",")[0]), Short.parseShort(data.split(",")[1])));

        return values;
    }

    private void generateMonsters(EntityFactory entityFactory) {
        Random random = new Random();
        IntStream.range(0, numberOfGroup).forEach(i -> {
            Collection<Monster> monsters = new ArrayList<>();
            byte groupSize = fixedGroupSize > 0 ? fixedGroupSize : (byte) random(minimumGroupSize, maximumGroupSize);

            IntStream.range(0, groupSize).forEach(inc -> {
                Pair<Integer, Short> randomPair = this.possibleGroups.get(random.nextInt(this.possibleGroups.size()));
                monsters.add(entityFactory.getMonsterTemplate(randomPair.getKey()).getByLevel(randomPair.getValue()));
            });
            register(new MonsterGroup(getNextId(), this, getRandomCell(), monsters));
        });
    }

    private String buildData() {
        StringBuilder packet = new StringBuilder();
        creatures.values().forEach(creature -> packet.append(GamePacketFormatter.showCreatureMessage(creature.getGm())).append("\n"));
        return packet.toString();
    }


    private void register(Creature creature) {
        this.creatures.put(creature.getId(), creature);
        creature.getLocation().getCell().getCreatures().add(creature.getId());
    }


    public int getNextId() {
        return idGenerator.incrementAndGet();
    }

    public void initializeNpc(Collection<Npc> npcCollection) {
        npcCollection.forEach(this::register);
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

}
