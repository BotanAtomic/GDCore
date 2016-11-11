package org.graviton.network.game.protocol;

import org.graviton.client.player.Player;
import org.graviton.utils.StringUtils;

import java.util.Collection;

/**
 * Created by Botan on 05/11/2016 : 22:56
 */
public class PlayerProtocol {

    public static String getPlayersPacketMessage(Collection<Player> players) {
        if (players == null || players.isEmpty())
            return "ALK31536000000|0";

        StringBuilder builder = new StringBuilder("ALK0|").append((players.size() == 1 ? 2 : players.size()));
        players.forEach(player -> builder.append(getALKMessage(player)));
        return builder.toString();
    }

    public static String getASKMessage(Player player) {
        StringBuilder builder = new StringBuilder("ASK|");
        builder.append(player.getId()).append('|');
        builder.append(player.getName()).append('|');
        builder.append(player.getLevel()).append('|');
        builder.append(player.getBreed().id()).append('|');
        builder.append(player.getSex()).append('|');
        builder.append(player.getSkin()).append('|');
        builder.append(StringUtils.toHex(player.getColor((byte) 1))).append("|");
        builder.append(StringUtils.toHex(player.getColor((byte) 2))).append("|");
        builder.append(StringUtils.toHex(player.getColor((byte) 3))).append("|");
        builder.append(formatItems());
        return builder.toString();
    }

    private static String getALKMessage(Player player) {
        return new StringBuilder("|").append(player.getId()).append(";").append(player.getName()).append(";").append(player.getLevel()).append(";").
                append(player.getSkin()).append(";").
                append(StringUtils.toHex(player.getColor((byte) 1))).append(";").
                append(StringUtils.toHex(player.getColor((byte) 2))).append(";").
                append(StringUtils.toHex(player.getColor((byte) 3))).append(";").
                append(getGMSMessage(player)).append(";;;;;;").toString();
    }

    private static String getGMSMessage(Player player) {
        return ",,,,";
    }

    private static String formatItems() {
        return "";
    }

}
