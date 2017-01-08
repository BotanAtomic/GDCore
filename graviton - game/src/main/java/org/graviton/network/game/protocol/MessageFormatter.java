package org.graviton.network.game.protocol;

import org.graviton.game.client.player.Player;

/**
 * Created by Botan on 16/11/2016 : 18:31
 */
public class MessageFormatter {

    public static String customMessage(String data) {
        return "Im".concat(data);
    }

    public static String lastInformationMessage(String lastConnection, String lastAddress) {
        return "Im0152;".concat(lastConnection).concat("~").concat(lastAddress);
    }

    public static String actualInformationMessage(String address) {
        return "Im0153;".concat(address);
    }

    public static String welcomeMessage() {
        return "Im189";
    }

    public static String maxPodsReached() {
        return "Im112";
    }

    public static String privateMessage(int playerId, String playerName, String message, boolean from) {
        return "cMK" + (from ? 'F' : 'T') + '|' + playerId + '|' + playerName + '|' + message;
    }

    public static String notConnectedPlayerMessage(String name) {
        return "cMEf" + name;
    }

    public static String buildChannelMessage(char channel, Player player, String message) {
        return "cMK" + channel + "|" + player.getId() + "|" + player.getName() + '|' + message;
    }

    public static String savedPositionMessage() {
        return "Im06";
    }

    public static String levelRequiredErrorMessage() {
        return "OAEL";
    }

    public static String conditionErrorMessage() {
        return "Im119|44";
    }
}
