package org.graviton.game.sellpoint;

import lombok.Data;
import org.graviton.database.entity.EntityFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Botan on 11/07/17. 03:30
 */

@Data
public class SellPointTemplate {
    private final EntityFactory entityFactory;

    private final short template;
    private final Map<Integer, SellPointLine> lines = new ConcurrentHashMap<>();

    public SellPointTemplate(short template, SellPointItem sellPointItem, EntityFactory entityFactory) {
        this.template = template;
        this.entityFactory = entityFactory;
        this.add(sellPointItem);
    }

    public SellPointLine getLine(int id) {
        return this.lines.get(id);
    }

    public void add(SellPointItem sellPointItem) {
        SellPointLine valid = this.lines.values().stream().filter(line -> line.checkStatistics(sellPointItem)).findFirst().orElse(null);

        if (valid != null)
            valid.add(sellPointItem);
        else {
            int lineId = entityFactory.nextSellPointLine();
            this.lines.put(lineId, new SellPointLine(lineId, sellPointItem));
        }
    }

    boolean remove(SellPointItem sellPointItem) {
        int lineId = sellPointItem.getLineId();

        final boolean result = this.lines.get(lineId).remove(sellPointItem);

        if (this.lines.get(sellPointItem.getLineId()).isEmpty())
            this.lines.remove(sellPointItem.getLineId());

        return result;
    }

    public boolean isEmpty() {
        return this.lines.isEmpty();
    }

    public List<SellPointItem> all() {
        return lines.values().stream().map(SellPointLine::all).flatMap(List::stream).collect(Collectors.toList());
    }
}
