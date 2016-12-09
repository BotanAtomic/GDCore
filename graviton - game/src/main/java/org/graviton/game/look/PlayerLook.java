package org.graviton.game.look;

import org.graviton.game.breeds.AbstractBreed;
import org.graviton.utils.Utils;
import org.jooq.Record;

import static org.graviton.database.jooq.login.tables.Players.PLAYERS;

/**
 * Created by Botan on 11/11/2016 : 21:37
 */
public class PlayerLook extends AbstractLook {
    private final AbstractBreed breed;
    private final byte sex;

    private short size;

    private byte title;

    public PlayerLook(Record record) {
        super(Utils.parseColors(record.get(PLAYERS.COLORS), ";"), record.get(PLAYERS.SKIN));
        this.breed = AbstractBreed.get(record.get(PLAYERS.BREED));
        this.sex = record.get(PLAYERS.SEX);
        this.size = record.get(PLAYERS.SIZE);
        this.title = record.get(PLAYERS.TITLE);
    }

    public PlayerLook(int[] colors, byte sex, AbstractBreed abstractBreed) {
        super(colors, abstractBreed.getDefaultSkin(sex));
        this.breed = abstractBreed;
        this.sex = sex;
        this.size = 100;
        this.title = 0;
    }

    public AbstractBreed getBreed() {
        return this.breed;
    }

    public byte getSex() {
        return this.sex;
    }

    public short getSize() {
        return this.size;
    }

    public void setSize(short size) {
        this.size = size;
    }

    public byte getTitle() {
        return this.title;
    }

}
