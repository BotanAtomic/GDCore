package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.collector.Collector;
import org.graviton.game.guild.Guild;
import org.graviton.game.guild.GuildMember;
import org.graviton.game.guild.GuildMember.GuildRight;
import org.graviton.game.spell.Spell;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GuildPacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;
import org.joda.time.Interval;

import java.util.Date;

/**
 * Created by Botan on 05/03/2017. 15:19
 */

@Slf4j
public class GuildHandler {
    private final GameClient client;

    public GuildHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, char subHeader) { // 'g'
        switch (subHeader) {
            case 'B':
                boostCharacteristic(data);
                break;

            case 'C':
                create(data);
                break;

            case 'H':
                placeCollector();
                break;

            case 'I':
                getInformation(data.charAt(0));
                break;

            case 'J':
                join(data);
                break;

            case 'P':
                promote(data.split("\\|"));
                break;

            case 'b':
                boostSpell(data);
                break;

            default:
                log.error("not implemented guild packet '{}'", subHeader);
        }
    }

    private void placeCollector() {
        Guild guild = client.getPlayer().getGuild();
        GuildMember guildMember = client.getPlayer().getGuild().getMember(client.getPlayer().getId());

        if (!guildMember.can(GuildRight.SET_COLLECTOR) /**|| guild.getMembers().size() < 10 **/) //todo : valid guild
            return;

        short price = (short) (1000 + (10 * guild.getLevel()));

        if (client.getPlayer().getInventory().getKamas() < price) {
            client.send(MessageFormatter.insufficientKamasMessage());
            return;
        }

        if (!client.getPlayer().getGameMap().canPlaceCollector()) {
            client.send(MessageFormatter.presentCollectorMessage());
            return;
        }

        if (client.getPlayer().getGameMap().getPlaces().length() < 5) {
            client.send(MessageFormatter.cannotFightMessage());
            return;
        }

        if (guild.getCollectors().size() >= guild.getMaxCollector())
            return;

        Date date;
        if ((date = guild.getCollectorPlaceTime().get(client.getPlayer().getMap().getId())) != null) {
            short minutes;
            if ((minutes = (short) new Interval(date.getTime(), new Date().getTime()).toDuration().getStandardMinutes()) < guild.getLevel() * 10) {
                client.send(MessageFormatter.collectorTimeWaitMessage(minutes));
                return;
            }
        }
        guild.getCollectorPlaceTime().put(client.getPlayer().getGameMap().getId(), new Date());

        client.getPlayer().getInventory().addKamas(-price);
        client.send(MessageFormatter.kamasCostMessage(price));
        client.send(PlayerPacketFormatter.asMessage(client.getPlayer()));
        Collector collector = new Collector(client.getPlayer().getGameMap().getNextId() + 500, guild, client.getPlayer().getLocation().copy(), client.getPlayer().getName());
        client.getPlayer().getGameMap().register(collector, true);

        guild.getCollectors().add(collector);

        guild.send(GuildPacketFormatter.collectorGuildMessage(guild.getCollectors()));
        guild.send(GuildPacketFormatter.newCollectorMessage(collector));
    }

    private void boostCharacteristic(String data) {
        Guild guild = client.getPlayer().getGuild();

        if (!guild.getMember(client.getPlayer().getId()).can(GuildRight.SET_BOOST))
            return;

        if (guild.getCapital() < 1)
            return;

        switch (data.charAt(0)) {
            case 'p':
                if (guild.getGuildStatistics().get(CharacteristicType.Prospection).total() >= 500)
                    return;
                guild.setCapital(guild.getCapital() - 1);
                guild.getGuildStatistics().get(CharacteristicType.Prospection).addContext((short) 1);
                break;
            case 'x':
                if (guild.getGuildStatistics().get(CharacteristicType.Wisdom).total() >= 400)
                    return;
                guild.setCapital(guild.getCapital() - 1);
                guild.getGuildStatistics().get(CharacteristicType.Wisdom).addContext((short) 1);
                break;
            case 'o':
                if (guild.getGuildStatistics().get(CharacteristicType.Pods).total() >= 5000)
                    return;
                guild.setCapital(guild.getCapital() - 1);
                guild.getGuildStatistics().get(CharacteristicType.Pods).addContext((short) 20);
                break;
            case 'k':
                if (guild.getCapital() < 10)
                    return;
                if (guild.getMaxCollector() >= 50)
                    return;
                guild.setCapital(guild.getCapital() - 10);
                guild.setMaxCollector(guild.getMaxCollector() + 1);
                break;
        }
        client.send(GuildPacketFormatter.characteristicMessage(guild));
        client.getEntityFactory().getGuildRepository().updateGuild(guild);
    }

    private void boostSpell(String data) {
        Guild guild = client.getPlayer().getGuild();
        short spellId = Short.parseShort(data);
        Spell spell = guild.getSpells().get(spellId);

        if (guild.getCapital() < 5 || (spell != null && spell.getLevel() == 5))
            return;

        guild.getSpells().put(spellId, spell == null ? client.getEntityFactory().getSpellTemplate(spellId).getLevel((byte) 1) : spell.next());
        guild.setCapital(guild.getCapital() - 5);

        client.send(GuildPacketFormatter.characteristicMessage(guild));
        client.getEntityFactory().getGuildRepository().updateGuild(guild);
    }

    private void join(String data) {
        switch (data.charAt(0)) {
            case 'R':
                Player target = client.getPlayerRepository().find(data.substring(1));

                if (target == null) {
                    client.send(GuildPacketFormatter.invitationErrorMessage('u'));
                    return;
                } else if (target.getGuild() != null) {
                    client.send(GuildPacketFormatter.invitationErrorMessage('a'));
                    return;
                } else if (!client.getPlayer().getGuild().getMember(client.getPlayer().getId()).can(GuildRight.CAN_INVITE)) {
                    client.send(GuildPacketFormatter.invitationErrorMessage('d'));
                    return;
                } else if (client.getPlayer().getGuild().getMembers().size() >= (40 + client.getPlayer().getGuild().getLevel())) {
                    client.send(GuildPacketFormatter.fullGuildMessage((short) (40 + client.getPlayer().getGuild().getLevel())));
                    return;
                }

                client.send(GuildPacketFormatter.invitationMessage(data.substring(1)));
                target.send(GuildPacketFormatter.invitationOtherMessage(client.getPlayer().getId(), client.getPlayer().getName(), client.getPlayer().getGuild().getName()));

                target.getAccount().getClient().getInteractionManager().setInteractionWith(client.getPlayer().getId());
                break;

            case 'E': {
                Player player = client.getPlayerRepository().find(client.getInteractionManager().getInteractionCreature());

                if (player == null)
                    return;

                player.send(GuildPacketFormatter.invitationErrorMessage('c'));
                break;
            }


            case 'K': {
                Player player = client.getPlayerRepository().find(client.getInteractionManager().getInteractionCreature());
                Guild guild = player.getGuild();
                GuildMember member = guild.addMember(new GuildMember(client.getPlayer(), guild, (byte) 0, (byte) 0));

                client.getEntityFactory().getGuildRepository().createGuildMember(member);

                client.send(GuildPacketFormatter.gsMessage(guild.getName(), guild.getEmblem(), member.getCompiledRight()));
                client.send(GuildPacketFormatter.personalJoinMessage());
                player.send(GuildPacketFormatter.joinMessage(guild.getName()));

                client.getPlayer().getGameMap().refreshCreature(client.getPlayer());
                break;
            }
        }
    }

    private void promote(String[] data) {
        GuildMember guildMember = client.getPlayer().getGuild().getMember(Integer.parseInt(data[0]));

        guildMember.setRank(Byte.parseByte(data[1]));
        guildMember.setGivenPercent(Byte.parseByte(data[2]));
        guildMember.parseRight(Integer.parseInt(data[3]));
        client.getEntityFactory().getGuildRepository().updateGuildMember(guildMember);
        if (guildMember.getPlayer() != null)
            guildMember.getPlayer().send(GuildPacketFormatter.gsMessage(guildMember));
    }

    private void getInformation(char data) {
        Guild guild = client.getPlayer().getGuild();
        switch (data) {
            case 'B':
                client.send(GuildPacketFormatter.characteristicMessage(guild));
                break;

            case 'G':
                client.send(GuildPacketFormatter.generalMessage(client.getEntityFactory().getExperience(guild.getLevel()), guild.getExperience(), guild.getLevel()));
                break;

            case 'M':
                client.send(GuildPacketFormatter.membersMessage(guild));
                break;

            case 'T':
                client.send(GuildPacketFormatter.collectorGuildMessage(guild.getCollectors()));
                break;
        }
    }

    private void create(String data) {
        Player player = client.getPlayer();

        if (player.getGuild() != null) {
            player.send(GuildPacketFormatter.errorMessage("a"));
            return;
        }

        String[] information = data.split("\\|");

        String compiledEmblem = "";

        for (int i = 0; i < 4; i++)
            compiledEmblem += Integer.toString(Integer.parseInt(information[i]), 36) + ",";

        String name = information[4];

        if (!client.getEntityFactory().getGuildRepository().checkName(name)) {
            player.send(GuildPacketFormatter.errorMessage("an"));
            return;
        }

        Guild guild = new Guild(client.getEntityFactory().getGuildRepository().nextId(), name, compiledEmblem);
        GuildMember member = guild.addMember(new GuildMember(client.getPlayer(), guild, (byte) 1, (byte) 1));

        client.getEntityFactory().getGuildRepository().createGuild(guild);
        client.getEntityFactory().getGuildRepository().createGuildMember(member);

        client.send(GuildPacketFormatter.gsMessage(name, compiledEmblem, member.getCompiledRight()));
        client.send(GuildPacketFormatter.gcMessage("k"));
        client.send(GuildPacketFormatter.gvMessage());

        client.getPlayer().getGameMap().refreshCreature(client.getPlayer());
    }


}
