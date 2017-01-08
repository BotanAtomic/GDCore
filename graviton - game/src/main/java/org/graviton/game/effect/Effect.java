package org.graviton.game.effect;

import org.graviton.game.fight.Fighter;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.spell.SpellEffect;

import java.util.Collection;

/**
 * Created by Botan on 25/12/2016. 23:56
 */
public interface Effect {
    void apply(Fighter fighter, Collection<Fighter> targets, Cell selectedCell, SpellEffect effect);
}
