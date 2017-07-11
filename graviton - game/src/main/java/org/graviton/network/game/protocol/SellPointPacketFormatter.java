package org.graviton.network.game.protocol;

import org.graviton.game.sellpoint.SellPoint;
import org.graviton.game.sellpoint.SellPointItem;
import org.graviton.game.sellpoint.SellPointLine;
import org.graviton.game.sellpoint.SellPointTemplate;

import java.util.List;

/**
 * Created by Botan on 26/06/17. 23:25
 */
public class SellPointPacketFormatter {

    public static String startMessage(SellPoint sellPoint, boolean sell) {
        return "ECK" + (sell ? 10 : 11) + "|1,10,100;" + sellPoint.getCategoriesData() + ";" + sellPoint.getStringTax() + ";" + sellPoint.getLevel() + ";100;-1;" + sellPoint.getExpiration();
    }

    public static String categoryMessage(byte category, String items) {
        return "EHL" + category + "|" + items;
    }

    private static String itemMessage(SellPointItem sellPointItem) {
        StringBuilder builder = new StringBuilder();
        builder.append(sellPointItem.getLineId()).append(";").append(sellPointItem.getItem().getQuantity()).append(";").append(sellPointItem.getItem().getTemplate().getId()).append(";")
                .append(sellPointItem.getItem().parseEffects()).append(";").append(sellPointItem.getPrice()).append(";350");
        return builder.toString();
    }

    private static String exchangeItemMessage(SellPointItem sellPointItem) {
        StringBuilder builder = new StringBuilder();
        builder.append(sellPointItem.getLineId()).append("|").append(sellPointItem.getItem().getQuantity()).append("|").append(sellPointItem.getItem().getTemplate().getId()).append("|")
                .append(sellPointItem.getItem().parseEffects()).append("|").append(sellPointItem.getPrice()).append("|350");
        return builder.toString();
    }

    public static String sellItemsMessage(List<SellPointItem> items) {
        StringBuilder builder = new StringBuilder("EL");
        items.forEach(item -> builder.append(itemMessage(item)).append("|"));
        return items.isEmpty() ? builder.toString() : builder.substring(0, builder.length() - 1);
    }

    public static String simpleItemAddMessage(SellPointItem sellPointItem) {
        return "EmK+" + exchangeItemMessage(sellPointItem);
    }

    public static String averagePriceMessage(short template, long averagePrice) {
        return "EHP" + template + "|" + averagePrice;
    }

    public static String linesMessage(SellPointTemplate sellPointTemplate, short template) {
        StringBuilder builder = new StringBuilder("EHl").append(template).append("|");
        sellPointTemplate.getLines().values().forEach(line -> builder.append(singleLineMessage(line)).append("|"));
        return builder.substring(0, builder.length() - 1);
    }

    private static String singleLineMessage(SellPointLine sellPointLine) {
        long[] price = sellPointLine.firstPrices();
        return String.valueOf(sellPointLine.getId()) + ";" + sellPointLine.getStatistics() + ";" + ((price[0] == 0) ? "" : price[0]) + ";" +
                ((price[1] == 0) ? "" : price[1]) + ";" + ((price[2] == 0) ? "" : price[2]);
    }


    private static String complexLineMessage(SellPointLine sellPointLine) {
        long[] price = sellPointLine.firstPrices();
        return String.valueOf(sellPointLine.getId()) + "|" + sellPointLine.getStatistics() + "|" + ((price[0] == 0) ? "" : price[0]) + "|" +
                ((price[1] == 0) ? "" : price[1]) + "|" + ((price[2] == 0) ? "" : price[2]);
    }



    public static String removeLineMessage(int line) {
        return "EHm-" + line;
    }

    public static String addLineMessage(SellPointLine sellPointLine) {
        return "EHm+" + complexLineMessage(sellPointLine);
    }
}
