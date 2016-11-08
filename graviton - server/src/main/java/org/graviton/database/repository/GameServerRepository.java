package org.graviton.database.repository;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.graviton.database.LoginDatabase;
import org.graviton.database.models.GameServer;
import org.graviton.network.exchange.ExchangeClient;
import org.graviton.network.exchange.protocol.ExchangeProtocol;
import org.graviton.network.exchange.state.State;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.graviton.database.jooq.login.tables.Servers.SERVERS;

/**
 * Created by Botan on 30/10/2016 : 13:37
 */

@Slf4j
public class GameServerRepository {
    @Getter
    private final Map<Byte, GameServer> gameServers;
    @Inject
    private LoginDatabase database;

    public GameServerRepository() {
        this.gameServers = new ConcurrentHashMap<>();
    }

    private void register(GameServer gameServer) {
        this.gameServers.put(gameServer.getId(), gameServer);
    }

    public void loadGameServers() {
        database.getResult(SERVERS).forEach(record -> register(new GameServer(record.getValue(SERVERS.ID), record.getValue(SERVERS.KEY))));
    }

    /**
     * @param data   Receive data form : I#key;#address;#port
     * @param client Exchange client of GameServer
     */
    public void setGameServerInformations(String data, ExchangeClient client) {
        String[] informations = data.split(";");
        GameServer gameServer = this.gameServers.get(Byte.parseByte(informations[0]));

        if (gameServer != null && gameServer.getKey().equals(informations[1])) {
            gameServer.setAddress(informations[2]);
            gameServer.setPort(Integer.parseInt(informations[3]));
            client.setGameServer(gameServer);
            gameServer.setExchangeClient(client);
            client.send(ExchangeProtocol.allowGameServer());
            client.setState(State.ONLINE);
            log.debug("Game server [{}] is successfully connected");
        } else {
            client.send(ExchangeProtocol.refuseGameServer());
            log.debug("Game server [{}] is refused");
        }
    }

}
