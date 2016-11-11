package org.graviton.client.player;

import lombok.Data;
import org.graviton.client.account.Account;
import org.graviton.client.player.breeds.AbstractBreed;
import org.graviton.game.look.PlayerLook;
import org.graviton.game.statistics.PlayerStatistics;
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

    private PlayerLook look;
    private String name;
    private byte level;
    private long experience;

    private PlayerStatistics statistics;

    public Player(Record record, Account account) {
        this.account = account;
        this.id = record.get(PLAYERS.ID);
        this.name = record.get(PLAYERS.NAME);

        this.look = new PlayerLook(record);
        this.statistics = new PlayerStatistics(record);

        this.level = record.get(PLAYERS.LEVEL);
        this.experience = record.get(PLAYERS.EXPERIENCE);
    }

    public Player(int id, String data, Account account) {
        String[] informations = data.split("\\|");

        this.account = account;
        this.id = id;
        this.name = informations[0];

        this.look = new PlayerLook(StringUtils.parseColors(informations[3] + ";" + informations[4] + ";" + informations[5]), Byte.parseByte(informations[2]), AbstractBreed.get(Byte.parseByte(informations[1])));
        this.statistics = new PlayerStatistics();

        this.level = 1;
        this.experience = 0;
    }

    public int getColor(byte color) {
        return this.look.getColors()[color - 1];
    }

    public AbstractBreed getBreed() {
        return this.look.getBreed();
    }

    public short getSkin() {
        return this.look.getSkin();
    }

    public byte getSex() {
        return this.look.getSex();
    }

    public byte getOrientation() {
        return this.look.getOrientation();
    }


}
