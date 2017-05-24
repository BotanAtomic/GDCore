package org.graviton.database.models;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.graviton.network.exchange.ExchangeClient;
import org.graviton.network.exchange.state.State;


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
    private State state;

    private ExchangeClient exchangeClient;

    public GameServer(byte id, String key) {
        this.id = id;
        this.key = key;
        this.state = State.OFFLINE;
        log.debug("game server [{}] : loaded", key);
    }

}

