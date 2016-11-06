package org.graviton.client.player;

import lombok.Data;
import org.graviton.client.account.Account;
import org.graviton.client.player.breeds.AbstractBreed;
import org.graviton.utils.StringUtils;
import org.jooq.Record;

import static org.graviton.database.jooq.login.tables.Players.PLAYERS;

/**
 * Created by Botan on 05/11/2016 : 22:57
 */
@Data
public class Player {
    private final int id;
    private final Account account;

    private String name;
    private AbstractBreed breed;
    private byte sex;
    private int skin;
    private int[] colors;
    private byte level;
    private long experience;
    private short statisticPoints;
    private short spellPoints;
    private int energy;

    private byte orientation;

    public Player(Record record, Account account) {
        this.account = account;

        this.id = record.get(PLAYERS.ID);
        this.name = record.get(PLAYERS.NAME);
        this.breed = AbstractBreed.get(record.get(PLAYERS.BREED));
        this.sex = record.get(PLAYERS.SEX);
        this.skin = record.get(PLAYERS.SKIN);
        this.colors = StringUtils.parseColors(record.get(PLAYERS.COLORS));
        this.level = record.get(PLAYERS.LEVEL);
        this.experience = record.get(PLAYERS.EXPERIENCE);
        this.statisticPoints = record.get(PLAYERS.STAT_POINTS);
        this.spellPoints = record.get(PLAYERS.SPELL_POINTS);
        this.energy = record.get(PLAYERS.ENERGY);
    }

    public int getColor(byte color) {
        return this.colors[color - 1];
    }


}
