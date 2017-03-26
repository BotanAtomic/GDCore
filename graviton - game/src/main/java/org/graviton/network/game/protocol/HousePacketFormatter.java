package org.graviton.network.game.protocol;

import org.graviton.database.entity.EntityFactory;
import org.graviton.game.client.account.Account;
import org.graviton.game.house.House;

import java.util.Collection;

/**
 * Created by Botan on 25/03/2017. 12:16
 */
public class HousePacketFormatter {

    public static String loadMessage(Collection<House> houses, EntityFactory entityFactory) {
        StringBuilder builder = new StringBuilder();
        houses.forEach(house -> singleLoadMessage(builder, house, entityFactory, true));
        return builder.toString();
    }

    public static String singleLoadMessage(StringBuilder builder, House house, EntityFactory entityFactory, boolean withSeparator) {
        builder.append("hP").append(house.getTemplate().getId()).append('|');
        if (house.getOwner() > 0) {
            Account account = entityFactory.getAccountRepository().find(house.getOwner());
            builder.append(account.getNickname()).append(";");
        } else
            builder.append(";");

        builder.append((house.getPrice() > 0 ? "1" : "0")).append(withSeparator ? "#" : "");
        return builder.toString();
    }

    public static String loadPersonalHouse(Collection<House> houses, boolean withSeparator) {
        StringBuilder builder = new StringBuilder();
        houses.forEach(house -> {
            builder.append("hL+|").append(house.getTemplate().getId()).append(";").append(house.getAccess()).append(";");
            builder.append((house.getPrice() > 0 ? "1" : "0")).append(";").append(house.getPrice()).append(withSeparator ? "#" : "");
        });
       return builder.toString();
    }

    public static String unloadPersonalHouse(int id) {
        return "hL-" + id;
    }

    public static String buyMessage(short houseId, long price) {
        return "hCK" + houseId + "|" + price;
    }

    public static String cancelBuyMessage() {
        return "hV";
    }

    public static String houseCodeMessage() {
        return "KCK0|8";
    }

    public static String houseLockCodeMessage() {
        return "KCK1|8";
    }

    public static String badHouseCodeMessage() {
        return "KKE";
    }

    public static String quitHouseCodeMessage() {
        return "KV";
    }

}
