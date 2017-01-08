package org.graviton.game.spell;

import lombok.Data;
import org.graviton.utils.Utils;

/**
 * Created by Botan on 25/12/2016. 02:39
 */

@Data
public class SpellView {
    private short place;
    private Spell spell;
    private byte position;

    public SpellView(short place, Spell spell, byte position) {
        this.place = place;
        this.spell = spell;
        this.position = position;
    }

    public String getPositionToString() {
        if (position == -1)
            return "";
        return String.valueOf(Utils.EXTENDED_ALPHABET.charAt(position));
    }

    public short getId() {
        return spell.getTemplate().getId();
    }

}
