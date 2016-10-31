package org.graviton.network.exchange.protocol;

/**
 * Created by Botan on 30/10/2016 : 13:43
 */
public class ExchangeProtocol {

    public static String needInformations() {
        return "?";
    }

    public static String allowGameServer() {
        return "SA";
    }

    public static String refuseGameServer() {
        return "SF";
    }

    public static String disconnectAccount(int account) {
        return "-" + account;
    }

    public static String connectAccount(int account) {
        return "+" + account;
    }
}
