package org.graviton.game.creature.monster;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.Creature;
import org.graviton.collection.CollectionQuery;
import org.graviton.constant.Dofus;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.drop.Drop;
import org.graviton.game.look.AbstractLook;
import org.graviton.game.look.enums.Orientation;
import org.graviton.game.maps.GameMap;
import org.graviton.game.position.Location;
import org.graviton.game.statistics.common.Statistics;
import org.graviton.network.game.protocol.MonsterPacketFormatter;
import org.joda.time.Interval;

import java.util.*;
import java.util.stream.Collectors;

import static org.graviton.game.alignment.type.AlignmentType.NEUTRE;


/**
 * Created by Botan on 02/12/2016. 23:20
 */
@Data
public class MonsterGroup implements Creature {
    private final int id;
    private final Collection<Monster> monsters;
    private Date creation = new Date();
    private Location location;

    private final long respawnTime;

    private GameMap gameMap;
    private short cell;

    public MonsterGroup(int id, GameMap gameMap, short cell, Collection<Monster> monsters) {
        this.id = id;
        this.location = new Location(gameMap, cell, Orientation.SOUTH_WEST);
        this.monsters = monsters;
        this.respawnTime = 0;
    }

    public MonsterGroup(int id, GameMap gameMap, short cell, Collection<Monster> monsters, long respawnTime) {
        this.id = id;
        this.gameMap = gameMap;
        this.cell = cell;
        this.monsters = monsters;
        this.respawnTime = respawnTime;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getGm(Player player) {
        return MonsterPacketFormatter.gmMessage(this);
    }

    @Override
    public void send(String data) {

    }

    @Override
    public Location getLocation() {
        return this.location == null ? (this.location = new Location(gameMap, cell, Orientation.SOUTH_WEST)) : this.location;
    }

    @Override
    public int getColor(byte color) {
        return 0;
    }

    @Override
    public Statistics getStatistics() {
        return null;
    }

    @Override
    public EntityFactory entityFactory() {
        return ((GameMap) location.getMap()).getEntityFactory();
    }

    @Override
    public AbstractLook look() {
        return null;
    }

    public short getCell() {
        return this.location.getCell().getId();
    }

    public Orientation getOrientation() {
        return this.location.getOrientation();
    }

    public short getStarsPercent() {
        return (short) (getStars() * 20);
    }

    public short getStars() {
        return (short) (new Interval(this.creation.getTime(), new Date().getTime()).toDuration().getStandardMinutes() / Dofus.STARS_TIME);
    }

    public long getBaseExperience() {
        return monsters.stream().mapToLong(Monster::getBaseExperience).sum();
    }

    public short getMaximumLevel() {
        return monsters.stream().max(Comparator.comparingInt(Monster::getLevel)).get().getLevel();
    }

    public int randomKamas() {
        int minimumKamas = monsters.stream().mapToInt(monster -> monster.getTemplate().getWinKamas()[0]).sum();
        int maximumKamas = monsters.stream().mapToInt(monster -> monster.getTemplate().getWinKamas()[1]).sum();
        return (int) (Math.random() * (maximumKamas - minimumKamas)) + minimumKamas;
    }

    public List<Drop> allDrops(EntityFactory entityFactory) {
        List<Drop> drops = new ArrayList<>(entityFactory.getDrops());
        monsters.forEach(monster -> drops.addAll(monster.getTemplate().getDrops().stream().map(drop -> drop.copy(monster.getGrade())).collect(Collectors.toList())));
        return CollectionQuery.from(drops).orderBy(Comparator.comparingDouble(Drop::getFinalChance)).reverse();
    }

    public MonsterGroup copy() {
        return new MonsterGroup(gameMap.getId(), getGameMap(), this.location.getCell().getId(), this.monsters.stream().map(Monster::copy).collect(Collectors.toList()), respawnTime);
    }

    public short aggressionDistance(byte playerAlignment) {
        return (short) monsters.stream().mapToInt(monster -> {
            byte distance = monster.getTemplate().getAggressionDistance();
            if (monster.getTemplate().getAlignment().getType() != NEUTRE && playerAlignment > 0  && playerAlignment != monster.getAlignment().getId())
                return distance + 13;
            return distance;
        }).sum();
    }

    public byte alignment() {
        return this.monsters.stream().findAny().get().getAlignment().getId();
    }

}
