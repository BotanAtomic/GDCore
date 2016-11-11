package org.graviton.game.look;

import lombok.Data;
import org.graviton.client.player.breeds.AbstractBreed;
import org.graviton.utils.StringUtils;
import org.jooq.Record;

import static org.graviton.database.jooq.login.tables.Players.PLAYERS;

/**
 * Created by Botan on 11/11/2016 : 21:37
 */
@Data
public class PlayerLook extends AbstractLook {
    private AbstractBreed breed;
    private byte sex;

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

}
