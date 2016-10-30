package org.graviton.network.login.protocol;

import org.graviton.database.models.GameServer;
import org.graviton.database.models.Player;

import java.util.Collection;

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

    public static String notAvailableNickname() {
        return "AlEs";
    }

    public static String nicknameInformationMessage(String nickname) {
        return "Ad" + nickname;
    }

    public static String communityInformationMessage(int community) {
        return "Ac" + community;
    }

    public static String serversInformationsMessage(Collection<GameServer> servers) {
        StringBuilder messageBuilder = new StringBuilder("AH");
        servers.forEach((server) -> messageBuilder.append(server.getId()).append(";").append(server.getState()).append(";110;").append(server.getState()).append("|"));
        return messageBuilder.toString();
    }

    public static String identificationSuccessMessage(boolean hasRights) {
        return "AlK" + (hasRights ? "1" : "0");
    }

    public static String accountQuestionInformationMessage(String question) {
        return "AQ" + question.replace(" ", "+");
    }

    public static String playersListMessage(Collection<Player> players, Collection<GameServer> servers) {
        StringBuilder messageBuilder = new StringBuilder("AxK0");
        servers.forEach(server -> messageBuilder.append("|").append(server.getId()).append(",").append(getNumberOfPlayer(players, server.getId())));
        return messageBuilder.toString();
    }

    private static int getNumberOfPlayer(Collection<Player> players, byte server) {
        final int[] number = {0};
        players.stream().filter(player -> player.getServer() == server).forEach(validPlayer -> number[0]++);
        return number[0];
    }
}
