package org.graviton.game.look;

import org.graviton.game.breeds.AbstractBreed;
import org.graviton.utils.StringUtils;
import org.jooq.Record;

import static org.graviton.database.jooq.login.tables.Players.PLAYERS;

/**
 * Created by Botan on 11/11/2016 : 21:37
 */
public class PlayerLook extends AbstractLook {
    private final AbstractBreed breed;
    private final byte sex;

    public PlayerLook(Record record) {
        super(StringUtils.parseColors(record.get(PLAYERS.COLORS)), record.get(PLAYERS.SKIN), record.get(PLAYERS.ORIENTATION));
        this.breed = AbstractBreed.get(record.get(PLAYERS.BREED));
        this.sex = record.get(PLAYERS.SEX);
    }

    public PlayerLook(int[] colors, byte sex, AbstractBreed abstractBreed) {
        super(colors, abstractBreed.getDefaultSkin(sex), (byte) 1);
        this.breed = abstractBreed;
        this.sex = sex;
    }

    public AbstractBreed getBreed() {
        return this.breed;
    }

    public byte getSex() {
        return this.sex;
    }

}
