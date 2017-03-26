package org.graviton.network.game.protocol;

import org.graviton.game.creature.collector.Collector;
import org.graviton.game.experience.Experience;
import org.graviton.game.guild.Guild;
import org.graviton.game.guild.GuildMember;
import org.graviton.game.spell.Spell;

import java.util.List;
import java.util.Map;

/**
 * Created by Botan on 05/03/2017. 15:22
 */
public class GuildPacketFormatter {

    public static String errorMessage(String error) {
        return "gCE" + error;
    }

    public static String gsMessage(String name, String emblem, String rights) {
        return "gS" + name + '|' + emblem.replace(',', '|') + rights;
    }

    public static String gsMessage(GuildMember guildMember) {
        return gsMessage(guildMember.getGuild().getName(), guildMember.getGuild().getEmblem(), guildMember.getCompiledRight());
    }

    public static String gcMessage(String extra) {
        return "gC" + extra;
    }

    public static String gvMessage() {
        return "gV";
    }

    public static String generalMessage(Experience experience, long currentExperience, short level) {
        long nextExperience = experience.getNext() == null ? -1 : experience.getNext().getGuild();
        return "gIG" + 1 + '|' + level + '|' + (experience.getGuild()) + '|' + currentExperience + '|' + nextExperience;
        //TODO : VALID GUILD
    }

    public static String membersMessage(Guild guild) {
        return "gIM+" + membersMessage(guild.getMembers());
    }

    private static String membersMessage(List<GuildMember> members) {
        StringBuilder builder = new StringBuilder();

        members.forEach(member -> {
            builder.append(member.getId()).append(';');
            builder.append(member.getName()).append(';');
            builder.append(member.getLevel()).append(';');
            builder.append(member.getSkin()).append(';');
            builder.append(member.getRank()).append(';');
            builder.append(member.getGivenExperience()).append(';');
            builder.append(member.getGivenPercent()).append(";");
            builder.append(member.getRight()).append(';');
            builder.append(member.getPlayer() == null ? "0" : member.getPlayer().getFight() == null ? "1" : "2").append(';');
            builder.append(member.getAlignment()).append(';');
            builder.append(member.getHoursOfLastConnection());
            builder.append('|');
        });

        return builder.substring(0, builder.length() - 1);
    }

    public static String characteristicMessage(Guild guild) {
        return "gIB" + guild.getMaxCollector() + "|" + guild.getCollectors().size() + "|" + (guild.getLevel() * 100) + "|" +
                guild.getLevel() + "|" + guild.getPods().total() + "|" + guild.getProspection().total() + "|" + guild.getWisdom().total() + "|" + guild.getMaxCollector() + "|" + guild.getCapital() + "|" +
                ((guild.getLevel() * 10) + 1000) + "|" + compileSpell(guild.getSpells());
    }

    private static String compileSpell(Map<Short, Spell> spells) {
        StringBuilder builder = new StringBuilder();
        spells.forEach((spellId, spell) -> builder.append(spellId).append(';').append(spell == null ? 0 : spell.getLevel()).append('|'));
        return builder.substring(0, builder.length() - 1);
    }

    public static String invitationErrorMessage(char error) {
        return "gJE" + error;
    }

    public static String fullGuildMessage(short limit) {
        return MessageFormatter.customMessage("155;" + limit);
    }

    public static String invitationMessage(String name) {
        return "gJR" + name;
    }

    public static String invitationOtherMessage(int targetId, String targetName, String guildName) {
        return "gJr" + targetId + '|' + targetName + '|' + guildName;
    }

    public static String joinMessage(String name) {
        return "gJKa" + name;
    }

    public static String personalJoinMessage() {
        return "gJKj";
    }

    public static String newCollectorMessage(Collector collector) {
        return "gTS" + Integer.toString(collector.getNames()[0], 36) + ","
                + Integer.toString(collector.getNames()[1], 36) + "|" + collector.getLocation().getMap().getId() + "|" +
                collector.getLocation().getMap().getPosition().replace(",", "|") + "|"
                + collector.getPlacer();
    }

    public static String collectorGuildMessage(List<Collector> collectors) {
        StringBuilder builder = new StringBuilder("gITM+");
        collectors.forEach(collector -> {
            builder.append(collector.getId()).append(';'); // id
            builder.append(Integer.toString(collector.getNames()[0], 36));
            builder.append(",");
            builder.append(Integer.toString(collector.getNames()[1], 36));

            builder.append(",");
            builder.append(collector.getPlacer()); // callerName
            builder.append(",");
            builder.append(collector.getPlaceTime().getTime()); // startDate
            builder.append(",");
            builder.append(",");
            builder.append("-1"); // lastHD
            builder.append(",");
            builder.append(collector.getPlaceTime().getTime() + collector.getGuild().getLevel() * 600000);
            builder.append(";");
            builder.append(Integer.toString(collector.getLocation().getMap().getId(), 36));
            builder.append(",");
            builder.append(collector.getLocation().getMap().getPosition());
            builder.append(";");

            builder.append(collector.getFight() != null ? 1 : 0);
            builder.append(";");

            if (collector.getFight() != null) {
                builder.append(0);//TimerActuel si combat
                builder.append(";");
                builder.append("45000");//TimerInit
                builder.append(";");
                builder.append(collector.getTeam().getCells().size() - 1);
                builder.append(";");
            } else
                builder.append("0;45000;7;");
            builder.append("|");

        });
        return builder.substring(0, builder.length() - 1);
    }

}
