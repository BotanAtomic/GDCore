package org.graviton.database.models;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Botan on 30/10/2016 : 00:17
 */
@Data
@Slf4j
public class GameServer {
    private final byte id;
    private final String key;

    private String address;
    private int port;
    private byte state;

    public GameServer(byte id, String key) {
        this.id = id;
        this.key = key;
        log.debug("Game server [{}] loaded successfully", key);
    }

}

