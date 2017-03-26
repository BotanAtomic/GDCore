package org.graviton.network.game.protocol;

/**
 * Created by Botan on 03/03/2017. 23:34
 */
public class ExchangePacketFormatter {

    public static String requestErrorMessage() {
        return "EREE";
    }

    public static String requestMessage(int playerId, int targetId, byte exchangeId) {
        return "ERK" + playerId + "|" + targetId + "|" + exchangeId;
    }

    public static String startMessage(byte exchangeId) {
        return "ECK" + exchangeId;
    }

    public static String cancelMessage() {
        return "EV";
    }

    public static String notificationMessage(boolean ready, int exchangerId) {
        return "EK" + (ready ? "1" : "0") + exchangerId;
    }

    public static String applyMessage() {
        return "EVa";
    }

    public static String editKamasMessage(long kamas) {
        return "EMKG" + kamas;
    }

    public static String editOtherKamasMessage(long kamas) {
        return "EmKG" + kamas;
    }

    public static String addItemMessage(int itemId, short quantity, int itemTemplate, String effects) {
        return "EMKO+" + itemId + "|" + quantity + "|" + itemTemplate + "|" + effects;
    }

    public static String addOtherItemMessage(int itemId, short quantity, int itemTemplate, String effects) {
        return "EmKO+" + itemId + "|" + quantity + "|" + itemTemplate + "|" + effects;
    }

    public static String removeItemMessage(int itemId) {
        return "EMKO-" + itemId;
    }

    public static String removeOtherItemMessage(int itemId) {
        return "EmKO-" + itemId;
    }

    public static String buyErrorMessage() {
        return "EBE";
    }

    public static String buySuccessMessage() {
        return "EBK";
    }

    public static String sellSuccessMessage() {
        return "ESK";
    }
}
