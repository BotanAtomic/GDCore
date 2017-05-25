package org.graviton.network.game.protocol;

import javafx.util.Pair;
import org.graviton.game.items.Item;
import org.graviton.game.items.Panoply;
import org.graviton.game.items.common.ItemEffect;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.lang.Language;
import org.graviton.lang.LanguageSentence;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Botan on 05/12/2016. 17:07
 */
public class ItemPacketFormatter {

    public static String quantityMessage(int item, short quantity) {
        return "OQ" + item + '|' + quantity;
    }

    static String formatItems(Collection<Item> items) {
        StringBuilder builder = new StringBuilder();
        items.forEach(object -> builder.append(object.parse()));
        return builder.toString();
    }

    public static String deleteMessage(int item) {
        return "OR" + item;
    }

    public static String addItemMessage(Item item) {
        return "OAKO" + item.parse();
    }

    public static String itemMovementMessage(long itemId, ItemPosition position) {
        return "OM" + itemId + "|" + (position.equippedWithoutBar() ? position.value() : "");
    }

    public static String panoplyMessage(Panoply panoply, Map<ItemEffect, Short> effects, byte equipped, Collection<Short> equippedItems) {
        StringBuilder builder = new StringBuilder("OS");

        if (equipped <= 0)
            return builder.append('-').append(panoply.getId()).toString();

        builder.append('+').append(panoply.getId()).append('|');
        panoply.getTemplates().keySet().stream().filter(equippedItems::contains).forEach(id -> builder.append(id).append(';'));

        return builder.substring(0, builder.length() - 1).concat("|").concat(panoply.effectToString(effects));

    }

    public static String giftMessage(List<Pair<ItemTemplate, Short>> values, Language language) {
        StringBuilder builder = new StringBuilder();
        AtomicInteger item = new AtomicInteger();
        values.forEach(pair -> {
            item.set(pair.getKey().getId());
            builder.append("1~").append(Integer.toString(pair.getKey().getId(), 16)).append("~").append(Integer.toString(pair.getValue(), 16)).append("~~")
                    .append(pair.getKey().parseStatistics()).append(";");
        });

        return "Ag1|" + item.get() + language.getSentence(LanguageSentence.GIFT) + (builder.length() > 0 ? builder.substring(0, builder.length() - 1) : builder.toString());
    }

    public static String giftAttributionSuccessMessage() {
        return "AG";
    }
}
