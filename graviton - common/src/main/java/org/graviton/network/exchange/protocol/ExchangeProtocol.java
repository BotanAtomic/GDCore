package org.graviton.network.exchange.protocol;

/**
 * Created by Botan on 30/10/2016 : 13:43
 */
public class ExchangeProtocol {

    /**
     * region #ASK
     **/

    public static String allowGameServer() {
        return "SA";
    }

    public static String refuseGameServer() {
        return "SF";
    }

    public static String disconnectAccount(int account) {
        return "-" + account;
    }

    /**
     * region #RESPONSE
     **/

    public static String informationMessage(byte serverId, String key, String address, int port) {
        return "I" + serverId + ";" + key.concat(";" + address + ";") + port;
    }
}
