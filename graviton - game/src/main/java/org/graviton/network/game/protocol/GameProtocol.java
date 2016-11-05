package org.graviton.network.game.protocol;

/**
 * Created by Botan on 05/11/2016 : 00:48
 */
public class GameProtocol {

    public static String helloGameMessage() {
        return "HG";
    }

    public static String accountTicketMessage(String key) {
        return "ATK".concat(key);
    }

}
