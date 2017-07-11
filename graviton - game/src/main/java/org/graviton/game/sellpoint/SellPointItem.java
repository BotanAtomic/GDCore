package org.graviton.game.sellpoint;

import lombok.Data;
import org.graviton.database.repository.PlayerRepository;
import org.graviton.game.items.Item;
import org.jooq.Record;

import java.util.Date;

import static org.graviton.database.jooq.game.tables.SellpointItems.SELLPOINT_ITEMS;

/**
 * Created by Botan on 10/07/17. 20:53
 */

@Data
public class SellPointItem implements Comparable<SellPointItem> {
    private Item item;
    private final long price;
    private Date date;

    private int owner;

    private byte sellPoint;

    private int lineId;

    public SellPointItem(Record record, PlayerRepository playerRepository) {
        this.item = playerRepository.loadItem(record.get(SELLPOINT_ITEMS.ID));
        this.price = record.get(SELLPOINT_ITEMS.PRICE);
        this.owner = record.get(SELLPOINT_ITEMS.OWNER);
        this.date = new Date(record.get(SELLPOINT_ITEMS.DATE));
    }

    public SellPointItem(Item item, int owner, long price) {
        this.item = item;
        this.price = price;
        this.owner = owner;
        this.date = new Date();
    }

    public byte getAmount() {
        return  (byte) (item.getQuantity() == 1 ? 1 : item.getQuantity() == 10 ? 2 : 3);
    }

    @Override
    public int compareTo(SellPointItem other) {
        long otherPrice = other.price;
        return otherPrice > price ? -1 : otherPrice == price ? 0 : otherPrice < price ? 1 : 0;
    }
}
