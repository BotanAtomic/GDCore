package org.graviton.game.creature.npc;

import lombok.Data;
import org.graviton.api.Creature;
import org.graviton.game.maps.GameMap;
import org.graviton.game.position.Location;
import org.graviton.network.game.protocol.NpcPacketFormatter;
import org.jooq.Record;

import static org.graviton.database.jooq.game.tables.Npcs.NPCS;

/**
 * Created by Botan on 27/11/16.
 */
@Data
public class Npc implements Creature {
    private final int id;
    private final NpcTemplate template;
    private final Location location;

    public Npc(NpcTemplate template, GameMap gameMap, Record record) {
        this.location = new Location(gameMap, record.get(NPCS.CELL), record.get(NPCS.ORIENTATION));
        this.template = template;
        this.id = gameMap.getNextId();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getGm() {
        return NpcPacketFormatter.gmMessage(this);
    }

    @Override
    public void send(String data) {

    }

    @Override
    public Location getLocation() {
        return this.location;
    }
}
