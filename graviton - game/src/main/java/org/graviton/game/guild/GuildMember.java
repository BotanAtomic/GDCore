package org.graviton.game.guild;

import lombok.Data;
import org.graviton.collection.CollectionQuery;
import org.graviton.game.client.player.Player;
import org.graviton.utils.Utils;
import org.joda.time.Interval;
import org.jooq.Record;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.graviton.database.jooq.game.tables.GuildMembers.GUILD_MEMBERS;

/**
 * Created by Botan on 05/03/2017. 21:06
 */

@Data
public class GuildMember {
    private Player player;

    private final int id;
    private final Guild guild;
    private final String name;

    private short level;
    private short skin;

    private byte rank;
    private int right;

    private long givenExperience;
    private byte givenPercent;
    private byte alignment;
    private Date lastConnection;
    private Map<GuildRight, Boolean> rights = new HashMap<>();

    public GuildMember(Record record, Guild guild) {
        this.id = record.get(GUILD_MEMBERS.ID);
        this.guild = guild;
        this.name = record.get(GUILD_MEMBERS.NAME);
        this.level = record.get(GUILD_MEMBERS.LEVEL);
        this.skin = record.get(GUILD_MEMBERS.SKIN);
        this.givenExperience = record.get(GUILD_MEMBERS.EXPERIENCE_GIVED);
        this.givenPercent = record.get(GUILD_MEMBERS.PERCENT_GIVED);
        this.alignment = record.get(GUILD_MEMBERS.ALIGNMENT);

        if (!record.get(GUILD_MEMBERS.LASTCONNECTION).isEmpty())
            this.lastConnection = Utils.parseDate("yyyy~MM~dd~HH~mm", record.get(GUILD_MEMBERS.LASTCONNECTION));

        this.rank = record.get(GUILD_MEMBERS.RANK);
        parseRight(record.get(GUILD_MEMBERS.RIGHTS));
    }

    public GuildMember(Player player, Guild guild, byte rank, byte right) {
        player.setGuild(guild);
        this.id = player.getId();
        this.guild = guild;
        this.name = player.getName();
        this.level = player.getLevel();
        this.skin = player.getSkin();
        this.givenExperience = 0;
        this.givenPercent = 0;
        this.alignment = player.getAlignment().getId();
        this.right = right;
        this.rank = rank;
        this.player = player;

        parseRight(right);
    }

    public void addExperience(long experience) {
        this.givenExperience += experience;
    }

    public String parseLastConnection() {
        return this.lastConnection == null ? "" : new SimpleDateFormat("yyyy~MM~dd~HH~mm").format(lastConnection);
    }

    public int getHoursOfLastConnection() {
        return (int) new Interval(lastConnection == null ? new Date().getTime() : lastConnection.getTime(), new Date().getTime()).toDuration().getStandardHours();
    }

    private void initializeRights(boolean give) {
        for (GuildRight right : GuildRight.values())
            rights.put(right, give);
    }

    public String getCompiledRight() {
        return Integer.toString(this.right, 36);
    }

    public GuildMember setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public void parseRight(int right) {
        initializeRights(false);

        if (right == 1) {
            initializeRights(true);
            this.right = 1;
            return;
        }

        this.right = right;

        while(right > 0) {
            for(GuildRight guildRight : CollectionQuery.from(Arrays.asList(GuildRight.values())).reverse()) {
                if(guildRight.value() <= right) {
                    right ^= guildRight.value();
                    rights.put(guildRight, true);
                    break;
                }
            }
        }
    }

    public boolean can(GuildRight guildRight) {
        return this.rights.get(guildRight);
    }


    public enum GuildRight {
        SET_BOOST(2),
        SET_RIGHT(4),
        CAN_INVITE(8),
        CAN_BAN(16),
        ALL_XP(32),
        CHANGE_XP(64),
        SET_RANK(128),
        SET_COLLECTOR(256),
        GET_COLLECTOR(512),
        /**
         * 1024 & 2048 ?
         **/
        USE_PARK(4096),
        ADJUST_PARK(8192),
        ADJUST_OTHER_MOUNT(16384);    //

        private final int id;

        GuildRight(int id) {
            this.id = id;
        }

        public int value() {
            return id;
        }

        public String toString() {
            return String.valueOf(id);
        }
    }
}
