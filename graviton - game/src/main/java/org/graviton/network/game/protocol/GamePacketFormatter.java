package org.graviton.network.game.protocol;

import org.graviton.game.fight.Fight;

import java.util.Collection;

/**
 * Created by Botan on 05/11/2016 : 00:48
 */
public class GamePacketFormatter {

    public static String helloGameMessage() {
        return "HG";
    }

    public static String accountTicketSuccessMessage(String key) {
        return "ATK".concat(key);
    }

    public static String accountTicketErrorMessage() {
        return "ATE";
    }

    public static String requestRegionalVersionMessage() {
        return "AVO";
    }

    public static String getQueuePositionMessage() {
        return "Af1|1|1|1|1";
    }

    public static String playerNameSuggestionSuccessMessage(String name) {
        return "APK" + name;
    }

    public static String playerDeleteFailedMessage() {
        return "ADE";
    }

    public static String gameCreationSuccessMessage() {
        return "GCK|1|";
    }

    public static String mapDataMessage(int id, String date, String key) {
        return "GDM|" + id + "|" + date + "|" + key;
    }

    public static String creatureChangeMapMessage(int id) {
        return "GA;2;" + id + ';';
    }

    public static String mapLoadedSuccessfullyMessage() {
        return "GDK";
    }

    public static String showCreatureMessage(String gm) {
        return "GM|+".concat(gm);
    }

    public static String hideCreatureMessage(int id) {
        return "GM|-" + id;
    }

    public static String regenTimerMessage(short time) {
        return "ILS" + time;
    }

    public static String addChannelsMessage(String canals) {
        return "cC+".concat(canals);
    }

    public static String creatureMovementMessage(short actionId, int actorId, String path) {
        return "GA" + actionId + ";1;" + actorId + ';' + path;
    }

    public static String noActionMessage() {
        return "GA;0";
    }

    public static String creationAnimationMessage() {
        return "TB";
    }

    public static String astrubAnimationMessage(int playerId) {
        return "GA;2" + ';' + playerId + ";7";
    }

    public static String changeOrientationMessage(int playerId, byte orientation) {
        return "eD" + playerId + '|' + orientation;
    }

    public static String updateAccessories(int player, String gms) {
        return "Oa" + player + '|' + gms;
    }

    public static String emoteMessage(int playerId, String data) {
        return "cS" + playerId + '|' + data;
    }

    public static String askDuelMessage(int playerId, int targetId) {
        return "GA;900;" + playerId + ';' + targetId;
    }

    public static String acceptDuelMessage(int playerId, int targetId) {
        return "GA;901;" + playerId + ';' + targetId;
    }


    public static String cancelDuelMessage(int playerId, int targetId) {
        return "GA;902;" + playerId + ';' + targetId;
    }


    public static String awayPlayerMessage(int targetId) {
        return "GA;903;" + targetId + ";o";
    }

    public static String fightCountMessage(byte numberOfFight) {
        return "fC" + numberOfFight;
    }

    public static String fightInformationMessage(Collection<Fight> fights) {
        StringBuilder builder = new StringBuilder("fL");
        fights.forEach(fight -> builder.append(fight.information()));
        return builder.substring(0, builder.length() - 1);
    }

    public static String fightDetailsMessage(Fight fight) {
        StringBuilder builder = new StringBuilder("fD");
        builder.append(fight.getId()).append("|");
        fight.getFirstTeam().getFighters().forEach(fighter -> builder.append(fighter.getName()).append('~').append(fighter.getLevel()).append(';'));
        builder.append('|');
        fight.getSecondTeam().getFighters().forEach(fighter -> builder.append(fighter.getName()).append('~').append(fighter.getLevel()).append(';'));
        return builder.toString();
    }

}
