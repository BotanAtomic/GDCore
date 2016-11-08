package org.graviton.network.game.protocol;

import org.graviton.client.player.Player;

import java.util.Collection;

/**
 * Created by Botan on 05/11/2016 : 22:56
 */
public class PlayerProtocol {

    public static String getPlayersPacketMessage(Collection<Player> players) {
        if (players == null || players.isEmpty())
            return "ALK31536000000|0";

        StringBuilder builder = new StringBuilder("ALK31536000000|").append((players.size() == 1 ? 2 : players.size()));
        players.forEach(player -> builder.append(getALKMessage(player)));
        return builder.toString();
    }

    private static String getALKMessage(Player player) {
        return new StringBuilder("|").append(player.getId()).append(";").append(player.getName()).append(";").append(player.getLevel()).append(";").
                append(player.getSkin()).append(";").
                append((player.getColor((byte) 1) != -1 ? Integer.toHexString(player.getColor((byte) 1)) : "-1")).append(";").
                append((player.getColor((byte) 2) != -1 ? Integer.toHexString(player.getColor((byte) 2)) : "-1")).append(";").
                append((player.getColor((byte) 3) != -1 ? Integer.toHexString(player.getColor((byte) 3)) : "-1")).append(";").
                append(getGMSMessage(player)).append(";;;;;;").toString();
    }

    private static String getGMSMessage(Player player) {
        return ",,,,";
    }

}
