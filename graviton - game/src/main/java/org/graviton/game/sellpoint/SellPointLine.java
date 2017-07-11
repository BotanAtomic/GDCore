package org.graviton.game.sellpoint;

import lombok.Data;
import org.graviton.game.items.common.ItemType;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.graviton.game.items.common.ItemType.FullSoulStone;

/**
 * Created by Botan on 11/07/17. 03:30
 */

@Data
public class SellPointLine {
    private final int id;
    private final short template;
    private final List<List<SellPointItem>> templates = new CopyOnWriteArrayList<>();
    private final String statistics;

    public SellPointLine(int lineId, SellPointItem sellPointItem) {
        this.id = lineId;
        this.template = sellPointItem.getItem().getTemplate().getId();
        this.statistics = sellPointItem.getItem().parseEffects();
        IntStream.range(0, 3).forEach(i -> templates.add(new CopyOnWriteArrayList<>()));
        this.add(sellPointItem);
    }

    public void add(SellPointItem sellPointItem) {
        sellPointItem.setLineId(this.id);
        final byte index = (byte) (sellPointItem.getAmount() - 1);
        this.templates.get(index).add(sellPointItem);
        Collections.sort(this.templates.get(index));
    }

    public boolean remove(SellPointItem sellPointItem) {
        byte index = (byte) (sellPointItem.getAmount() - 1);
        boolean result = this.templates.get(index).remove(sellPointItem);
        Collections.sort(this.templates.get(index));
        return result;
    }

    public boolean checkStatistics(SellPointItem sellPointItem) {
        return this.statistics.equalsIgnoreCase(sellPointItem.getItem().parseEffects()) && sellPointItem.getItem().getTemplate().getType() != FullSoulStone;
    }

    public boolean isEmpty() {
        return this.templates.isEmpty() || this.templates.stream().filter(line -> !line.isEmpty()).count() == 0;
    }

    public List<SellPointItem> all() {
        return this.templates.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public long[] firstPrices() {
        final long[] result = new long[3];
        IntStream.range(0, 3).forEach(i -> {
            List<SellPointItem> data = this.templates.get(i);
            result[i] = data.isEmpty() ? 0 : data.get(0).getPrice();
        });
        return result;
    }

    public SellPointItem getSellPointItem(byte amount, long price) {
        return all().stream().filter(item -> item.getPrice() == price && item.getAmount() == amount).findAny().orElse(null);
    }
}
