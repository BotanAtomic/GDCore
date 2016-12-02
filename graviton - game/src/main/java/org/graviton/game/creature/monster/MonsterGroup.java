package org.graviton.game.creature.monster;

import org.graviton.api.Creature;
import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.game.maps.GameMap;
import org.graviton.game.position.Location;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Botan on 02/12/2016. 23:20
 */
public class MonsterGroup implements Creature {
    private final int id;
    private final Collection<Monster> monsters;
    private Location location;

    public MonsterGroup(GameMap gameMap, short cell, String monsters) {
        this.id = gameMap.getNextId();
        this.location = new Location(gameMap, cell, OrientationEnum.EAST);
        this.monsters = this.generate();
    }

    private Collection<Monster> generate() {
        Collection<Monster> monsters = new ArrayList<>();
        return monsters;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getGm() {
        return "";
    }

    @Override
    public void send(String data) {

    }

    @Override
    public Location getLocation() {
        return this.location;
    }
}
