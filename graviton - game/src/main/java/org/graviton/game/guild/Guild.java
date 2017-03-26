package org.graviton.game.guild;

import lombok.Data;
import org.graviton.constant.Dofus;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.creature.collector.Collector;
import org.graviton.game.spell.Spell;
import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.type.GuildStatistics;
import org.jooq.Record;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.graviton.database.jooq.game.tables.Guilds.GUILDS;

/**
 * Created by Botan on 05/03/2017. 15:16
 */

@Data
public class Guild {
    private final int id;
    private String name, emblem;

    private short level;
    private long experience;
    private int capital;

    private int maxCollector;

    private final Map<Short,Spell> spells = new HashMap<>();
    private final GuildStatistics guildStatistics;

    private List<GuildMember> members;

    private Map<Integer,Date> collectorPlaceTime = new HashMap<>();

    private List<Collector> collectors = new ArrayList<>();

    public Guild(int id, String name, String emblem){
         this.id = id;
         this.name = name;
         this.emblem = emblem;
         this.experience = 0;
         this.level = 1;
         this.capital = 0;
         this.guildStatistics = new GuildStatistics(this, null);
         this.members = new CopyOnWriteArrayList<>();
         initializeSpell(Dofus.GUILD_SPELLS, null);
    }

    public Guild(Record record, EntityFactory entityFactory) {
        this.id = record.get(GUILDS.ID);
        this.name = record.get(GUILDS.NAME);
        this.emblem = record.get(GUILDS.EMBLEM);
        this.experience = record.get(GUILDS.CAPITAL);
        this.level = record.get(GUILDS.LEVEL);
        this.capital = record.get(GUILDS.CAPITAL);
        this.maxCollector = record.get(GUILDS.LIMIT);

        this.guildStatistics = new GuildStatistics(this, record);
        initializeSpell(record.get(GUILDS.SPELLS), entityFactory);
    }

    public Guild setMembers(List<GuildMember> members) {
        this.members = members;
        return this;
    }

    private void initializeSpell(String data, EntityFactory entityFactory) {
        for(String spellData : data.split("\\|")) {
            short spellId = Short.parseShort(spellData.split(";")[0]);
            byte level = Byte.parseByte(spellData.split(";")[1]);
            this.spells.put(spellId, level == 0 ? null : entityFactory.getSpellTemplate(spellId).getLevel(level));
        }
    }

    public String parseSpells() {
        StringBuilder builder = new StringBuilder();
        spells.forEach((spellId, spell) -> {
            builder.append(spellId).append(';').append(spell == null ? 0 : spell.getLevel()).append('|');
        });
        return builder.substring(0, builder.length() - 1);
    }

    public void addExperience(long experience, EntityFactory entityFactory) {
        this.experience += experience;

        while (experience > entityFactory.getExperience(this.level).getNext().getGuild() && this.level < Dofus.MAX_LEVEL)
            this.upLevel();

        entityFactory.getGuildRepository().updateGuild(this);
    }

    private void upLevel() {
        this.level++;
        this.capital += 5;
    }

    public GuildMember addMember(GuildMember member) {
        this.members.add(member);
        return member;
    }

    public Characteristic getPods() {
        return this.guildStatistics.get(CharacteristicType.Pods);
    }

    public Characteristic getProspection() {
        return this.guildStatistics.get(CharacteristicType.Prospection);
    }

    public Characteristic getWisdom() {
        return this.guildStatistics.get(CharacteristicType.Wisdom);
    }

    public GuildMember getMember(int id) {
        Optional<GuildMember> memberOptional = this.members.stream().filter(member -> member.getId() == id).findAny();
        return memberOptional.isPresent() ? memberOptional.get() : null;
    }

    public void send(String data) {
        this.members.stream().filter(member -> member.getPlayer() != null).forEach(member -> member.getPlayer().send(data));
    }

}
