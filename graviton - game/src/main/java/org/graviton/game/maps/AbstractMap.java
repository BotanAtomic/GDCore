package org.graviton.game.maps;

import org.graviton.api.Creature;
import org.graviton.game.maps.cell.Cell;

import java.util.Map;

/**
 * Created by Botan on 14/12/2016. 16:15
 */
public interface AbstractMap {
    int getId();

    void send(String data);

    void out(Creature creature);

    void refreshCreature(Creature creature);

    void enter(Creature creature);

    void load(Creature creature);

    Creature getCreature(int id);

    String buildData();

    Map<Short, Cell> getCells();

    byte getWidth();

    String getPosition();
}
