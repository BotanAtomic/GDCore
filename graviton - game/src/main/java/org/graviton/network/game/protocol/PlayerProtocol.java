package org.graviton.network.game.protocol;

import org.graviton.client.player.Player;
import org.graviton.constant.Dofus;
import org.graviton.game.alignement.Alignement;
import org.graviton.game.experience.Experience;
import org.graviton.game.statistics.PlayerStatistics;
import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.utils.StringUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Botan on 05/11/2016 : 22:56
 */
public class PlayerProtocol {

    public static String getPlayersPacketMessage(Collection<Player> players) {
        if (players == null || players.isEmpty())
            return "ALK0|0";

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
        builder.append(StringUtils.toHex(player.getColor((byte) 1))).append('|');
        builder.append(StringUtils.toHex(player.getColor((byte) 2))).append('|');
        builder.append(StringUtils.toHex(player.getColor((byte) 3))).append('|');
        builder.append(formatItems());
        return builder.toString();
    }

    private static String getALKMessage(Player player) {
        return new StringBuilder("|").append(player.getId()).append(';').append(player.getName()).append(';').append(player.getLevel()).append(';').
                append(player.getSkin()).append(';').
                append(StringUtils.toHex(player.getColor((byte) 1))).append(';').
                append(StringUtils.toHex(player.getColor((byte) 2))).append(';').
                append(StringUtils.toHex(player.getColor((byte) 3))).append(';').
                append(getGMSMessage(player)).append(";;;;;;").toString();
    }

    private static String getGMSMessage(Player player) {
        return ",,,,";
    }

    private static String formatItems() {
        return "";
    }

    public static String getAsMessage(Player player, Experience experience, Alignement alignement, PlayerStatistics statistics) {
        StringBuilder builder = new StringBuilder("As");

        builder.append(player.getExperience()).append(',');
        builder.append(experience.getPlayer()).append(',');
        builder.append(experience.getNext().getPlayer()).append('|');

        builder.append(player.getKamas()).append('|');
        builder.append(player.getStatistics().getStatisticPoints()).append('|');
        builder.append(player.getStatistics().getSpellPoints()).append('|');

        builder.append(alignement.getId()).append('~').append(alignement.getId()).append(',');
        builder.append(alignement.getAlignementLevel()).append(',');
        builder.append(alignement.getGrade()).append(',');
        builder.append(alignement.getHonor()).append(',');
        builder.append(alignement.getDishonor()).append(',');
        builder.append(alignement.isEnabled() ? "1|" : "0|");

        builder.append(statistics.getCurrentLife()).append(',');
        builder.append(statistics.getMaxLife()).append('|');

        builder.append(statistics.getEnergy()).append(',');
        builder.append(Dofus.MAX_ENERGY).append('|');

        builder.append(statistics.getInitiative()).append('|');
        builder.append(statistics.getProspection()).append('|');

        Arrays.asList(CharacteristicType.ActionPoints, CharacteristicType.MovementPoints).forEach(characteristic -> builder.append(statistics.get(characteristic).base()).append(',').append(statistics.get(characteristic).equipment()).append(',').append(statistics.get(characteristic).gift()).append(',').append(statistics.get(characteristic).context()).append(',').append(statistics.get(characteristic).total()).append('|'));

        CharacteristicType.asBuild.forEach(value -> {
            Characteristic characteristic = statistics.get(value);
            builder.append(characteristic.base()).append(',').append(characteristic.equipment()).append(',').append(characteristic.gift()).append(',').append(characteristic.context()).append('|');
        });

        return builder.toString();
    }

}
