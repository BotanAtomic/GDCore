package org.graviton.game.creature.npc.exchange;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Botan on 24/05/17. 15:24
 */

@Data
class ExchangeEntity {
    private final List<ItemExchangeTemplate> needs, gives;

    ExchangeEntity(String data) {
        this.needs = configure(data.split("\\|")[0]);
        this.gives = configure(data.split("\\|")[1]);
    }


    private List<ItemExchangeTemplate> configure(String data) {
       return Stream.of(data.split(",")).map(value -> new ItemExchangeTemplate(Short.parseShort(value.split(":")[0]), Short.parseShort(value.split(":")[1]))).collect(Collectors.toList());
    }

    List<ItemExchangeTemplate> getGives(byte coefficient) {
        if(coefficient == 1)
            return gives;
        return gives.stream().map(objectExchange -> new ItemExchangeTemplate(objectExchange.getItemId(), (short) (objectExchange.getQuantity() * coefficient))).collect(Collectors.toList());
    }


}
