package org.graviton.network.game.protocol;

/**
 * Created by Botan on 16/11/2016 : 18:31
 */
public class MessageProtocol {

    public static String lastInformationsMessage(String lastConnection, String lastAddress) {
        return "Im0152;".concat(lastConnection).concat("~").concat(lastAddress);
    }

    public static String actualInformationsMessage(String address) {
        return "Im0153;".concat(address);
    }

    public static String welcomeMessage() {
        return "Im189";
    }
}
