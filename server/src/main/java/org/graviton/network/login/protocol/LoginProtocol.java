package org.graviton.network.login.protocol;

/**
 * Created by Botan on 29/10/2016 : 23:12
 */
public class LoginProtocol {

    public static String helloConnect(String key) {
        return "HC".concat(key);
    }

    public static String badClientVersion(String requiredVersion) {
        return "AlEv".concat(requiredVersion);
    }

    public static String accessDenied() {
        return "AlEf";
    }

    public static String banned() {
        return "AlEb";
    }

    public static String alreadyConnected() {
        return "AlEc";
    }

    public static String emptyNickname() {
        return "AlEr";
    }
}
