package org.graviton.network.game.protocol;

import org.graviton.constant.Dofus;
import org.graviton.game.alignment.Alignment;
import org.graviton.game.client.player.Player;
import org.graviton.game.experience.Experience;
import org.graviton.game.fight.Fighter;
import org.graviton.game.items.Item;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.game.maps.GameMap;
import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.type.PlayerStatistics;
import org.graviton.utils.Cells;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.graviton.utils.Utils.toHex;

/**
 * Created by Botan on 05/11/2016 : 22:56
 */
public class PlayerPacketFormatter {

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
        builder.append(toHex(player.getColor((byte) 1))).append('|');
        builder.append(toHex(player.getColor((byte) 2))).append('|');
        builder.append(toHex(player.getColor((byte) 3))).append('|');
        builder.append(ItemPacketFormatter.formatItems(player.getInventory().values()));
        return builder.toString();
    }

    private static String alkMessage(Player player) {
        return "|" + player.getId() + ';' + player.getName() + ';' + player.getLevel() + ';' + player.getSkin() + ';' + toHex(player.getColor((byte) 1))
                + ';' + toHex(player.getColor((byte) 2)) + ';' + toHex(player.getColor((byte) 3)) + ';' + gmsMessage(player) + ";;;;;;";
    }

    public static String gmsMessage(Player player) {
        StringBuilder builder = new StringBuilder();
        ItemPosition[] positions = {ItemPosition.Weapon, ItemPosition.Hat, ItemPosition.Cloak, ItemPosition.Pet, ItemPosition.Shield};
        for (ItemPosition position : positions) {
            Item item;
            if ((item = player.getInventory().getByPosition(position)) != null)
                builder.append(Integer.toHexString(item.getTemplate().getId()));
            builder.append(",");
        }
        return builder.toString().substring(0, builder.length() - 1);
    }

    public static String asMessage(Player player) {
        return asMessage(player, player.getEntityFactory().getExperience(player.getLevel()), player.getAlignment(), player.getStatistics());
    }


    public static String asMessage(Player player, Experience experience, Alignment alignment, PlayerStatistics statistics) {
        StringBuilder builder = new StringBuilder("As");

        builder.append(player.getExperience()).append(',');
        builder.append(experience.getPlayer()).append(',');
        builder.append(experience.getNext() == null ? experience.getPlayer() : experience.getNext().getPlayer()).append('|');

        builder.append(player.getInventory().getKamas()).append('|');
        builder.append(player.getStatistics().getStatisticPoints()).append('|');
        builder.append(player.getStatistics().getSpellPoints()).append('|');

        builder.append(alignment.getId()).append('~').append(alignment.getId()).append(',');
        builder.append(alignment.getAlignmentLevel()).append(',');
        builder.append(alignment.getGrade()).append(',');
        builder.append(alignment.getHonor()).append(',');
        builder.append(alignment.getDishonor()).append(',');
        builder.append(alignment.isEnabled() ? "1|" : "0|");

        builder.append(statistics.getLife().getCurrent()).append(',');
        builder.append(statistics.getLife().getMaximum()).append('|');

        builder.append(statistics.getEnergy()).append(',');
        builder.append(Dofus.MAX_ENERGY).append('|');

        builder.append(statistics.get(CharacteristicType.Initiative).total()).append('|');
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
        builder.append(player.getAlignment().getId()).append(",0,");
        builder.append((player.getAlignment().isEnabled() ? player.getAlignment().getGrade() : "0")).append(",");
        builder.append(player.getLevel() + player.getId());

        if (player.getAlignment().isEnabled() && player.getAlignment().getDishonor() > 0)
            builder.append(",").append(1).append(';');
        else
            builder.append(";");

        builder.append(toHex(player.getColor((byte) 1))).append(";");
        builder.append(toHex(player.getColor((byte) 2))).append(";");
        builder.append(toHex(player.getColor((byte) 3))).append(";");
        builder.append(gmsMessage(player)).append(";");
        builder.append(player.getLevel() > 99 ? (player.getLevel() > 199 ? (2) : (1)) : (0)).append(";;;");

        if (player.getGuild() != null)
            builder.append(player.getGuild().getName()).append(";").append(player.getGuild().getEmblem()).append(";");
        else
            builder.append(";;");

        builder.append("0;;;");
        return builder.toString();
    }

    public static String fightGmMessage(Fighter fighter) {
        Player player = (Player) fighter.getCreature();
        StringBuilder builder = new StringBuilder();

        builder.append(player.getBreed().id()).append(';');
        builder.append(player.getSkin()).append('^').append(player.getSize()).append(';');
        builder.append(player.getSex()).append(';');
        builder.append(player.getLevel()).append(';');
        builder.append(player.getAlignment().getId()).append(",0,");
        builder.append(player.getAlignment().isEnabled() ? player.getAlignment().getGrade() : 0).append(',').append(fighter.getId()).append(';');
        builder.append(toHex(player.getCreature().getColor((byte) 1))).append(";");
        builder.append(toHex(player.getCreature().getColor((byte) 2))).append(";");
        builder.append(toHex(player.getCreature().getColor((byte) 3))).append(";");

        builder.append(gmsMessage(player)).append(";");

        builder.append(player.getStatistics().getLife().getCurrent()).append(';');
        builder.append(fighter.getStatistics().get(CharacteristicType.ActionPoints).total()).append(';');
        builder.append(fighter.getStatistics().get(CharacteristicType.MovementPoints).total()).append(';');
        builder.append(fighter.getStatistics().get(CharacteristicType.ResistancePercentNeutral).total()).append(';');
        builder.append(fighter.getStatistics().get(CharacteristicType.ResistancePercentEarth).total()).append(';');
        builder.append(fighter.getStatistics().get(CharacteristicType.ResistancePercentFire).total()).append(';');
        builder.append(fighter.getStatistics().get(CharacteristicType.ResistancePercentWater).total()).append(';');
        builder.append(fighter.getStatistics().get(CharacteristicType.ResistancePercentWind).total()).append(';');
        builder.append(fighter.getStatistics().get(CharacteristicType.DodgeActionPoints).total()).append(';');
        builder.append(fighter.getStatistics().get(CharacteristicType.DodgeMovementPoints).total()).append(';');
        builder.append(fighter.getSide().ordinal()).append(';');
        builder.append(";\n"); // todo mounts
        return builder.toString();
    }

    public static String fightCloneGmMessage(Fighter fighter, Fighter clone) {
        Player player = (Player) fighter.getCreature();
        StringBuilder builder = new StringBuilder();

        builder.append(player.getBreed().id()).append(';').append(player.getSkin()).append('^').append(player.getSize()).append(';');
        builder.append(player.getSex()).append(';');
        builder.append(player.getLevel()).append(';');
        builder.append(player.getAlignment().getId()).append(",0,");
        builder.append(player.getAlignment().isEnabled() ? player.getAlignment().getGrade() : 0).append(',').append(clone.getId()).append(';');
        builder.append(toHex(player.getCreature().getColor((byte) 1))).append(";");
        builder.append(toHex(player.getCreature().getColor((byte) 2))).append(";");
        builder.append(toHex(player.getCreature().getColor((byte) 3))).append(";");

        builder.append(gmsMessage(player)).append(";");

        builder.append(clone.getStatistics().getLife().getCurrent()).append(';');
        builder.append(clone.getStatistics().get(CharacteristicType.ActionPoints).total()).append(';');
        builder.append(clone.getStatistics().get(CharacteristicType.MovementPoints).total()).append(';');
        builder.append(clone.getStatistics().get(CharacteristicType.ResistancePercentNeutral).total()).append(';');
        builder.append(clone.getStatistics().get(CharacteristicType.ResistancePercentEarth).total()).append(';');
        builder.append(clone.getStatistics().get(CharacteristicType.ResistancePercentFire).total()).append(';');
        builder.append(clone.getStatistics().get(CharacteristicType.ResistancePercentWater).total()).append(';');
        builder.append(clone.getStatistics().get(CharacteristicType.ResistancePercentWind).total()).append(';');
        builder.append(clone.getStatistics().get(CharacteristicType.DodgeActionPoints).total()).append(';');
        builder.append(clone.getStatistics().get(CharacteristicType.DodgeMovementPoints).total()).append(';');
        builder.append(clone.getSide().ordinal()).append(';');
        builder.append(";\n"); // todo mounts
        return builder.toString();
    }

    public static String restrictionMessage() {
        return "AR6bk";
    }

    public static String podsMessage(short[] pods) {
        return "Ow" + pods[0] + "|" + pods[1];
    }

    public static String nextLevelMessage(short level) {
        return "AN" + level;
    }

    public static String alignmentMessage(byte alignment) {
        return "ZS" + alignment;
    }

    public static String disableAlignmentMessage(short looseHonor) {
        return "GIP" + looseHonor;
    }

    public static String newAlignmentMessage(byte alignment) {
        return "ZC" + alignment;
    }

    public static String dishonorMessage() {
        return MessageFormatter.customMessage("084;1");
    }


    public static String zaapListMessage(List<Integer> zaaps, GameMap currentGameMap) {
        StringBuilder builder = new StringBuilder("WC");
        zaaps.forEach(i -> builder.append("|").append(i).append(";").append(i == currentGameMap.getId() ? 0 : Cells.getZaapCost(currentGameMap, currentGameMap.getEntityFactory().getMap(i))));
        return builder.toString();
    }

    public static String quitZaapMenuMessage() {
        return "WV";
    }


}
