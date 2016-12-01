package org.graviton.database.repository;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.graviton.database.AbstractDatabase;
import org.graviton.database.GameDatabase;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.creature.npc.Npc;
import org.graviton.game.maps.GameMap;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.graviton.database.jooq.game.tables.Maps.MAPS;
import static org.graviton.database.jooq.game.tables.Npcs.NPCS;


/**
 * Created by Botan on 13/11/2016 : 17:29
 */
public class GameMapRepository {
    private final Map<Integer, GameMap> maps;

    private GameDatabase database;

    @Inject
    private EntityFactory entityFactory;

    @Inject
    public GameMapRepository(@Named("database.game") AbstractDatabase database) {
        this.maps = new ConcurrentHashMap<>();
        this.database = (GameDatabase) database;
    }

    public GameMap get(int id) {
        if (maps.containsKey(id))
            return maps.get(id);
        return load(id);
    }

    private GameMap load(int id) {
        GameMap maps = new GameMap(database.getRecord(MAPS, MAPS.ID.equal(id)));
        maps.initializeNpc(getNpc(maps));
        this.maps.put(maps.getId(), maps);
        return maps;
    }

    private Collection<Npc> getNpc(GameMap gameMap) {
        return database.getResult(NPCS, NPCS.MAP.equal(gameMap.getId())).stream().
                map(record -> new Npc(entityFactory.getNpcTemplate(record.get(NPCS.ID)), gameMap, record)).
                collect(Collectors.toList());
    }

}
