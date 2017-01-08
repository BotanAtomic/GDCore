package org.graviton.network.game.protocol;

import org.graviton.game.spell.SpellView;

import java.util.Map;

/**
 * Created by Botan on 25/12/2016. 02:13
 */
public class SpellPacketFormatter {

    public static String spellListMessage(Map<Short, SpellView> spells) {
        StringBuilder builder = new StringBuilder("SL");

        spells.forEach((id, viewer) -> {
            builder.append(viewer.getId()).append('~');
            builder.append(viewer.getSpell().getLevel()).append('~');
            builder.append(viewer.getPositionToString());
            builder.append(";");
        });

        return builder.substring(0, builder.length() - 1);
    }

    public static String boostSpellErrorMessage() {
        return "SUE";
    }

    public static String boostSpellSuccessMessage(short spellId, byte spellLevel) {
        return "SUK" + spellId + "~" + spellLevel;
    }
}
