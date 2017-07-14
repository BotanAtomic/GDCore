package org.graviton.game.creature;

import org.graviton.database.entity.EntityFactory;
import org.graviton.game.client.player.Player;
import org.graviton.game.look.AbstractLook;
import org.graviton.game.position.Location;
import org.graviton.game.statistics.common.Statistics;

/**
 * Created by Botan on 13/11/2016 : 17:51
 */
public interface Creature {
    int getId();

    String getGm(Player player);

    void send(String data);

    Location getLocation();

    void setLocation(Location location);

    int getColor(byte color);

    Statistics getStatistics();

    EntityFactory entityFactory();

    AbstractLook look();
}
