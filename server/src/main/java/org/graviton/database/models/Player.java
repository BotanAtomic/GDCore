package org.graviton.database.models;

import lombok.Data;

/**
 * Created by Botan on 30/10/2016 : 00:17
 */
@Data
public class Player {
    private final int id;
    private final byte server;

    public Player(int id, byte server) {
        this.id = id;
        this.server = server;
    }
}
