package org.graviton.game.creature.monster;

import lombok.Data;
import org.graviton.api.Creature;
import org.graviton.collection.CollectionQuery;
import org.graviton.constant.Dofus;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.drop.Drop;
import org.graviton.game.look.AbstractLook;
import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.game.maps.GameMap;
import org.graviton.game.position.Location;
import org.graviton.game.statistics.common.Statistics;
import org.graviton.network.game.protocol.MonsterPacketFormatter;
import org.joda.time.Interval;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Botan on 02/12/2016. 23:20
 */
@Data
public class MonsterGroup implements Creature {
    private final int id;
    private final Collection<Monster> monsters;
    private Date creation = new Date();
    private Location location;

    public MonsterGroup(int id, GameMap gameMap, short cell, Collection<Monster> monsters) {
        this.id = id;
        this.location = new Location(gameMap, cell, OrientationEnum.random());
        this.monsters = monsters;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getGm() {
        return MonsterPacketFormatter.gmMessage(this);
    }

    @Override
    public void send(String data) {

    }

    @Override
    public Location getLocation() {
        return this.location;
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

    public OrientationEnum getOrientation() {
        return this.location.getOrientation();
    }

    public short getStarsPercent() {
        return (short) (getStars() / Dofus.STARS_TIME);
    }

    public short getStars() {
        return (short) (new Interval(this.creation.getTime(), new Date().getTime()).toDuration().getStandardMinutes() * 20);
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

}
