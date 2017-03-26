package org.graviton.game.house;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.network.game.protocol.HousePacketFormatter;
import org.jooq.Record;

import static org.graviton.database.jooq.game.tables.HousesData.HOUSES_DATA;
/**
 * Created by Botan on 25/03/2017. 10:27
 */

@Data
public class House {
    private final HouseTemplate template;
    private int owner;
    private long price;
    private int access;
    private String key;

    public House(HouseTemplate template, Record record) {
        this.template = template;
        this.owner = record.get(HOUSES_DATA.OWNER);
        this.price = record.get(HOUSES_DATA.SALE);
        this.access = record.get(HOUSES_DATA.ACCESS);
        this.key = record.get(HOUSES_DATA.KEY);
    }

    public void open(Player player, String key) {
        if(key.equals(this.key) || player.getAccount().getId() == this.owner)
            player.changeMap(template.getHouseMap(), template.getHouseCell());
         else {
            player.send(HousePacketFormatter.badHouseCodeMessage());
            player.send(HousePacketFormatter.quitHouseCodeMessage());
        }
    }
}
