package org.graviton.game.spell;


import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Created by Botan on 28/12/2016. 20:59
 */
public class SpellList extends TreeMap<Short, SpellView> {

    public SpellList(Map<Short, SpellView> spells) {
        super.putAll(spells);
    }

    public void add(SpellView spellView) {
        super.put(spellView.getPlace(), spellView);
    }

    public SpellView get(short spell) {
        Optional<SpellView> result = values().stream().filter(viewer -> viewer.getId() == spell).findFirst();
        return result.isPresent() ? result.get() : null;
    }

    public SpellView get(byte position) {
        Optional<SpellView> result = values().stream().filter(viewer -> viewer.getPosition() == position).findFirst();
        return result.isPresent() ? result.get() : null;
    }

}
