package org.graviton.api;

import org.graviton.game.position.Location;

/**
 * Created by Botan on 13/11/2016 : 17:51
 */
public interface Creature {
    int getId();

    String getGm();

    void send(String data);

    Location getLocation();
}
