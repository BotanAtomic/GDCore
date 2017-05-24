package org.graviton.game.creature.npc.exchange;

import org.graviton.game.client.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Botan on 24/05/17. 15:08
 */
public class ExchangeParser {

    private final List<ExchangeEntity> exchangeEntities = new CopyOnWriteArrayList<>();

    public ExchangeParser(String exchanges) {
        Stream.of(exchanges.split("~")).forEach(data -> exchangeEntities.add(new ExchangeEntity(data)));
    }

    public List<ItemExchangeTemplate> check(Player player, Map<Integer, Short> items) {
        return exchangeEntities.stream().map(entry -> {
            List<Byte> coefficient = new ArrayList<>();

            AtomicInteger validate = new AtomicInteger(0);

            entry.getNeeds().forEach(exchangeEntity -> items.keySet().forEach(item -> {
                short quantity = items.get(item);
                byte currentCoefficient = (byte) ((quantity % exchangeEntity.getQuantity()) == 0 ? quantity / exchangeEntity.getQuantity() : 1);
                coefficient.add(currentCoefficient);

                if(exchangeEntity.getQuantity() * currentCoefficient == quantity && player.getInventory().get(item).getTemplate().getId() == exchangeEntity.getItemId())
                    validate.incrementAndGet();
            }));

            return validate.get() == entry.getNeeds().size() ? entry.getGives(coefficient.stream().min(Integer::compare).orElse((byte) 1)) : new ArrayList<ItemExchangeTemplate>();
        }).flatMap(List::stream).collect(Collectors.toList());
    }


}
