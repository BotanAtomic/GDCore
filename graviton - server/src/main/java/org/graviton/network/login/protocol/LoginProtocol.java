package org.graviton.network.login.protocol;


import org.graviton.database.models.GameServer;
import org.graviton.database.models.Player;
import org.graviton.network.login.LoginClient;

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
        return "AlEa";
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

    public static String communityInformationMessage() {
        return "Ac0";
    }

    public static String serversInformationMessage(Collection<GameServer> servers) {
        StringBuilder messageBuilder = new StringBuilder("AH");
        servers.forEach((server) -> messageBuilder.append(server.getId()).append(";").append(server.getState().value()).append(";110;1|"));
        return messageBuilder.toString();
    }

    public static String selectGameServerMessage(int accountId, GameServer gameServer) {
        return "AYK".concat(gameServer.getAddress()).concat(":") + gameServer.getPort() + ";" + accountId;
    }

    public static String notAvailableGameServer() {
        return "AXEd";

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

    public static String searchPlayerMessage(Collection<Player> players, LoginClient client) {
        StringBuilder messageBuilder = new StringBuilder("AF");
        client.getGameServerRepository().getGameServers().values().stream().filter(server -> getNumberOfPlayer(players, server.getId()) != 0).forEach(playerSever -> messageBuilder.append(playerSever.getId()).append(",").append(getNumberOfPlayer(players, playerSever.getId())).append(";"));
        return messageBuilder.toString();
    }

    private static int getNumberOfPlayer(Collection<Player> players, byte server) {
        final int[] number = {0};
        players.stream().filter(player -> player.getServer() == server).forEach(validPlayer -> number[0]++);
        return number[0];
    }
}
