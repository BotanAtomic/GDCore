package org.graviton.game.trunk.type;

import lombok.Data;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.client.player.Player;
import org.graviton.game.exchange.type.TrunkExchange;
import org.graviton.game.house.House;
import org.graviton.game.trunk.AbstractTrunk;
import org.graviton.network.game.protocol.ExchangePacketFormatter;
import org.graviton.network.game.protocol.HousePacketFormatter;
import org.jooq.Record;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.graviton.database.jooq.game.tables.Trunks.TRUNKS;
/**
 * Created by Botan on 21/05/17. 11:21
 */

@Data
public class Trunk extends AbstractTrunk {
    private final short id;
    private int owner;
    private String key;

    private short house;

    private AtomicBoolean inUse = new AtomicBoolean(false);

    public Trunk(Record record, EntityFactory entityFactory) {
        super(record.get(TRUNKS.KAMAS));
        this.id = record.get(TRUNKS.ID);
        this.key = record.get(TRUNKS.KEY);
        this.owner = record.get(TRUNKS.OWNER);
        this.house = record.get(TRUNKS.HOUSE);
        Stream.of(record.get(TRUNKS.ITEMS).split(";")).filter(data -> !data.isEmpty()).forEach(data -> super.add(entityFactory.getPlayerRepository().loadItem(Integer.parseInt(data))));
        entityFactory.getGameMapRepository().find(record.get(TRUNKS.MAP)).addTrunk(this, record.get(TRUNKS.CELL));
    }

    public void setInUse(boolean value) {
        this.inUse.set(value);
    }

    public boolean inUse() {
        return this.inUse.get();
    }

    public void open(Player player, String key) {
        if(key.equals(this.key)) {
            player.send(HousePacketFormatter.quitHouseCodeMessage());
            player.send(ExchangePacketFormatter.startMessage((byte) 5));
            player.send(ExchangePacketFormatter.trunkMessage(this));
            player.setExchange(new TrunkExchange(player, this));
            setInUse(true);
        } else {
            player.send(HousePacketFormatter.badHouseCodeMessage());
            player.send(HousePacketFormatter.quitHouseCodeMessage());
        }
    }


}
