package org.graviton.network.game.protocol;

import org.graviton.game.client.player.Player;
import org.graviton.game.group.Group;
import org.graviton.game.statistics.common.CharacteristicType;

/**
 * Created by Botan on 29/01/2017. 11:48
 */
public class PartyPacketFormatter {

    public static String errorMessage(String extra) {
        return "PIE" + extra;
    }

    public static String invitationMessage(String firstName, String secondName) {
        return "PIK" + firstName + '|' + secondName;
    }

    public static String quitPartyMessage() {
        return "IH";
    }

    public static String kickPartyMessage(String id) {
        return "PV" + id;
    }

    public static String cancelMessage() {
        return "PR";
    }

    public static String createMessage(String chiefName) {
        return "PCK" + chiefName;
    }

    public static String informationMessage(int chiefId) {
        return "PL" + chiefId;
    }

    public static String pmMessage(Player player) {
        StringBuilder builder = new StringBuilder();
        builder.append(player.getId()).append(';');
        builder.append(player.getName()).append(';');
        builder.append(player.getSkin()).append(';');
        builder.append(player.getColor((byte) 1)).append(';');
        builder.append(player.getColor((byte) 2)).append(';');
        builder.append(player.getColor((byte) 3)).append(';');
        builder.append(ItemPacketFormatter.formatItems(player.getInventory().getEquippedItems())).append(';');
        builder.append(player.getLife().getCurrent()).append(",").append(player.getLife().getMaximum()).append(';');
        builder.append(player.getLevel()).append(';');
        builder.append(player.getInitiative()).append(';');
        builder.append(player.getStatistics().get(CharacteristicType.Prospection).total()).append(';');
        builder.append('0');//Side = ?
        return builder.toString();
    }

    public static String buildPMMessage(Group group) {
        StringBuilder builder = new StringBuilder("PM+");
        group.forEach(player -> builder.append(pmMessage(player)).append('|'));
        return builder.substring(0, builder.length() - 1);
    }

    public static String singlePmMessage(Player player) {
        return "PM+" + pmMessage(player);
    }

    public static String singlePmMessage(int playerId) {
        return "PM-" + playerId;
    }

    private static String locatePlayer(Player player) {
        return player.getMap().getPosition().replace(',', ';') + ';' + player.getMap().getId() + ";2;" + player.getId() + ';' + player.getName();
    }

    public static String locationMessage(Group group) {
        StringBuilder builder = new StringBuilder("IH");
        group.forEach(player -> builder.append(locatePlayer(player)).append('|'));
        return builder.substring(0, builder.length() - 1);
    }

    public static String followClientMessage(String name, boolean all) {
        return all ? MessageFormatter.customMessage("0178") : MessageFormatter.customMessage("052;" + name);
    }


    public static String unfollowClientMessage(String name) {
        return MessageFormatter.customMessage("053;" + name);
    }

    public static String followMessage(int id) {
        return "PF+" + id;
    }

    public static String unfollowMessage() {
        return "PF-";
    }

    public static String flagMessage(String position) {
        return "IC" + position.replace(',', '|');
    }

}
