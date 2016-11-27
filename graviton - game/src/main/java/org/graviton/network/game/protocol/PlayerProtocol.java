package org.graviton.network.game.protocol;

import org.graviton.constant.Dofus;
import org.graviton.game.alignement.Alignement;
import org.graviton.game.client.player.Player;
import org.graviton.game.experience.Experience;
import org.graviton.game.statistics.PlayerStatistics;
import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.utils.StringUtils;

import java.util.Arrays;
import java.util.Collection;

import static org.graviton.utils.StringUtils.toHex;

/**
 * Created by Botan on 05/11/2016 : 22:56
 */
public class PlayerProtocol {

    public static String playersPacketMessage(Collection<Player> players) {
        if (players == null || players.isEmpty())
            return "ALK0|0";

        StringBuilder builder = new StringBuilder("ALK0|").append((players.size() == 1 ? 2 : players.size()));
        players.forEach(player -> builder.append(alkMessage(player)));
        return builder.toString();
    }

    public static String askMessage(Player player) {
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

    private static String alkMessage(Player player) {
        return new StringBuilder("|").append(player.getId()).append(';').append(player.getName()).append(';').append(player.getLevel()).append(';').
                append(player.getSkin()).append(';').
                append(toHex(player.getColor((byte) 1))).append(';').
                append(toHex(player.getColor((byte) 2))).append(';').
                append(toHex(player.getColor((byte) 3))).append(';').
                append(gmsMessage(player)).append(";;;;;;").toString();
    }

    private static String gmsMessage(Player player) {
        return ",,,,";
    }

    private static String formatItems() {
        return "";
    }

    public static String asMessage(Player player, Experience experience, Alignement alignement, PlayerStatistics statistics) {
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

    public static String gmMessage(Player player) {
        StringBuilder builder = new StringBuilder();
        builder.append(player.getCell().getId());
        builder.append(";").append(player.getOrientation().ordinal());
        builder.append(";0;").append(player.getId()).append(";");
        builder.append(player.getName()).append(";").append(player.getBreed().id());
        builder.append((player.getTitle() != 0 ? ("," + player.getTitle()) : ""));
        builder.append(";").append(player.getSkin()).append("^").append(player.getSize()).append(";").append(player.getSex()).append(";");
        builder.append(player.getAlignement().getId()).append(",0,");
        builder.append((player.getAlignement().isEnabled() ? player.getAlignement().getGrade() : "0")).append(",");
        builder.append(player.getLevel() + player.getId());

        if (player.getAlignement().isEnabled() && player.getAlignement().getDishonor() > 0)
            builder.append(",").append(1).append(';');
        else
            builder.append(";");

        builder.append(toHex(player.getColor((byte) 1))).append(";");
        builder.append(toHex(player.getColor((byte) 2))).append(";");
        builder.append(toHex(player.getColor((byte) 3))).append(";");
        builder.append(gmsMessage(player)).append(";");
        builder.append(player.getLevel() > 99 ? (player.getLevel() > 199 ? (2) : (1)) : (0)).append(";;;");
        builder.append(";;0;;;"); //TODO : guild
        return builder.toString();
    }

    public static String alignmentMessage(byte id) {
        return "ZL" + id;
    }

    public static String restrictionMessage() {
        return "AR6bk";
    }

    public static String podsMessage(short[] pods) {
        return "Ow" + pods[0] + "|" + pods[1];
    }
}
