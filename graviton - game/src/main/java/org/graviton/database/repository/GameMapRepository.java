package org.graviton.database.repository;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.graviton.database.AbstractDatabase;
import org.graviton.database.GameDatabase;
import org.graviton.game.maps.GameMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.graviton.database.jooq.game.tables.Maps.MAPS;

/**
 * Created by Botan on 13/11/2016 : 17:29
 */
public class GameMapRepository {
    private final Map<Integer, GameMap> maps;

    private GameDatabase database;

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
        this.maps.put(maps.getId(), maps);
        return maps;
    }
}
