package org.graviton.network.game.protocol;

import org.graviton.game.inventory.PlayerStore;
import org.graviton.game.items.Item;
import org.graviton.game.trunk.AbstractTrunk;
import org.graviton.game.trunk.type.Bank;

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

    public static String startMerchantMessage(byte exchangeId, int seller) {
        return "ECK" + exchangeId + "|" + seller;
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

    public static String trunkMessage(AbstractTrunk trunk) {
        StringBuilder builder = new StringBuilder("EL");
        trunk.forEach(item -> builder.append('O').append(item.parse()).append(';'));
        if(trunk.getKamas() > 0)
            builder.append('G').append(trunk.getKamas());
        return builder.toString();
    }

    public static String personalStoreMessage(PlayerStore store) {
        StringBuilder builder = new StringBuilder();
        store.forEach(item -> builder.append(item.getId()).append(";").append(item.getItem().getQuantity()).append(";").append(item.getItem().getTemplate().getId())
                .append(";").append(item.getItem().parseEffects()).append(";").append(item.getPrice()).append("|"));
        return "EL" + (builder.length() == 0 ? builder.toString() : builder.substring(0, builder.length() - 1));

    }

    public static String addTrunkItemMessage(int itemId, short quantity, int itemTemplate, String effects) {
        return "EsKO+" + itemId + "|" + quantity + "|" + itemTemplate + "|" + effects;
    }

    public static String simpleRemoveItemMessage(int id) {
        return "EsKO-" + id;
    }

    public static String trunkKamasEditMessage(long kamas) {
        return "EsKG" + kamas;
    }

    public static String merchantMessage(long tax) {
        return "Eq1|1|" + tax;
    }
}
