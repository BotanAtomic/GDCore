package org.graviton.network.game.protocol;

import org.graviton.game.client.player.Player;

/**
 * Created by Botan on 16/11/2016 : 18:31
 */
public class MessageFormatter {

    public static String customMessage(String data) {
        return "Im".concat(data);
    }

    public static String customStaticMessage(String message) {
        return "Im00;".concat(message);
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

    public static String insufficientLevelForIncarnam() {
        return customMessage("1127");
    }

    public static String insufficientKamasMessage() {
        return "Im182";
    }

    public static String levelRequiredErrorMessage() {
        return "OAEL";
    }

    public static String conditionErrorMessage() {
        return "Im119|44";
    }

    public static String whiteConsoleMessage(String message) {
        return "BAT0" + message;
    }

    public static String greenConsoleMessage(String message) {
        return "BAT2" + message;
    }

    public static String redConsoleMessage(String message) {
        return "BAT1" + message;
    }

    public static String looseEnergyMessage(short energy) {
        return customMessage("034;" + energy);
    }

    public static String presentCollectorMessage() {
        return "Im1168;1";
    }

    public static String cannotFightMessage() {
        return "Im113";
    }

    public static String collectorTimeWaitMessage(int minutes) {
        return "Im1167;" + minutes;
    }

    public static String kamasCostMessage(int cost) {
        return "Im046;" + cost;
    }

    public static String cannotLaunchSpellMessage() {
        return customMessage("1175");
    }

    public static String notHaveSpellMessage() {
        return customMessage("1169");
    }

    public static String needMoreActionPointMessage(byte currentPoint, byte needPoint) {
        return customMessage("1170;" + currentPoint + "~" + needPoint);
    }

    public static String badRangeMessage(byte minimum, byte maximum, byte current) {
        return customMessage("1171;" + minimum + "~" + maximum + "~" + current);
    }

    public static String busyCellMessage() {
        return customMessage("1172");
    }

    public static String notInlineMessage() {
        return customMessage("1173");
    }

    public static String obstacleOnLineMessage() {
        return customMessage("1174");
    }

    public static String regenLifeMessage(int life) {
        return customMessage("01" + ";" + life);
    }

    public static String kickedOfHouseMessage(String kicker) {
        return customMessage("018;" + kicker);
    }

    public static String newZaapMessage() {
        return customMessage("024");
    }

    public static String cannotUseZaapMessage() {
        return customMessage("183");
    }
}
