package org.graviton.database.repository;

import com.google.inject.Inject;
import org.graviton.database.Database;
import org.graviton.database.Repository;
import org.graviton.database.api.GameDatabase;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.guild.Guild;
import org.graviton.game.guild.GuildMember;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static org.graviton.database.jooq.game.tables.GuildMembers.GUILD_MEMBERS;
import static org.graviton.database.jooq.game.tables.Guilds.GUILDS;

/**
 * Created by Botan on 05/03/2017. 15:25
 */
public class GuildRepository extends Repository<Integer, Guild> {
    private Database database;

    @Inject private EntityFactory entityFactory;

    @Inject
    public GuildRepository(@GameDatabase Database database) {
        this.database = database;
    }

    public void createGuild(Guild guild) {
        super.add(guild.getId(), guild);
        database.getDslContext().insertInto(GUILDS, GUILDS.ID, GUILDS.NAME, GUILDS.EMBLEM).values(guild.getId(), guild.getName(), guild.getEmblem()).execute();
    }

    public void updateGuild(Guild guild) {
        database.update(GUILDS).set(GUILDS.LEVEL, guild.getLevel()).set(GUILDS.EXPERIENCE, guild.getExperience()).set(GUILDS.CAPITAL, guild.getCapital())
                .set(GUILDS.LIMIT, guild.getMaxCollector()).set(GUILDS.WISDOM, guild.getWisdom().context()).set(GUILDS.PODS, guild.getPods().context())
                .set(GUILDS.PROSPECTION, guild.getProspection().context()).set(GUILDS.SPELLS, guild.parseSpells()).execute();
    }

    public void createGuildMember(GuildMember guildMember) {
        database.getDslContext().insertInto(GUILD_MEMBERS, GUILD_MEMBERS.ID, GUILD_MEMBERS.GUILD, GUILD_MEMBERS.NAME, GUILD_MEMBERS.LEVEL,
                GUILD_MEMBERS.SKIN, GUILD_MEMBERS.RANK, GUILD_MEMBERS.RIGHTS, GUILD_MEMBERS.ALIGNMENT, GUILD_MEMBERS.LASTCONNECTION)
                .values(guildMember.getId(), guildMember.getGuild().getId(), guildMember.getName(), guildMember.getLevel(),
                        guildMember.getSkin(), guildMember.getRank(), guildMember.getRight(), guildMember.getAlignment(), guildMember.parseLastConnection()).execute();
    }

    public void updateGuildMember(GuildMember guildMember) {
        database.update(GUILD_MEMBERS)
                .set(GUILD_MEMBERS.RANK, guildMember.getRank())
                .set(GUILD_MEMBERS.RIGHTS, guildMember.getRight())
                .set(GUILD_MEMBERS.LASTCONNECTION, guildMember.parseLastConnection())
                .set(GUILD_MEMBERS.EXPERIENCE_GIVED, guildMember.getGivenExperience())
                .set(GUILD_MEMBERS.PERCENT_GIVED, guildMember.getGivenPercent())
                .set(GUILD_MEMBERS.LEVEL, guildMember.getLevel())
                .set(GUILD_MEMBERS.ALIGNMENT, guildMember.getAlignment())
                .where(GUILD_MEMBERS.ID.equal(guildMember.getId())).execute();
    }

    public int load() {
        return (int) database.getResult(GUILDS).stream().filter(record -> {
            Guild guild = new Guild(record, entityFactory);
            add(guild.getId(), guild.setMembers(loadMembers(guild)));
            return true;
        }).count();
    }

    private List<GuildMember> loadMembers(Guild guild) {
        return database.getResult(GUILD_MEMBERS, GUILD_MEMBERS.GUILD.equal(guild.getId())).stream().map(record -> new GuildMember(record, guild)).collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    }

    public int nextId() {
        return database.getNextId(GUILDS, GUILDS.ID);
    }

    public boolean checkName(String name) {
        return database.getResult(GUILDS, GUILDS.NAME.equal(name)).isEmpty();
    }

    @Override
    public Guild find(Object value) {
        return get((int) value);
    }
}
