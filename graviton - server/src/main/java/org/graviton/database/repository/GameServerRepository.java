package org.graviton.database.repository;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.graviton.database.api.LoginDatabase;
import org.graviton.database.Database;
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
    @Getter private final Map<Byte, GameServer> gameServers;

    private Database database;

    @Inject public GameServerRepository(@LoginDatabase Database database) {
        this.gameServers = new ConcurrentHashMap<>();
        this.database = database;
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
    public void setGameServerInformation(String data, ExchangeClient client) {
        String[] information = data.split(";");
        GameServer gameServer = this.gameServers.get(Byte.parseByte(information[0]));

        if (gameServer != null && gameServer.getKey().equals(information[1])) {
            gameServer.setAddress(information[2]);
            gameServer.setPort(Integer.parseInt(information[3]));
            client.setGameServer(gameServer);
            gameServer.setExchangeClient(client);
            client.send(ExchangeProtocol.allowGameServer());
            client.setState(State.ONLINE);
            log.debug("Game server [{}] is successfully connected", gameServer.getKey());
        } else {
            client.send(ExchangeProtocol.refuseGameServer());
            log.debug("Game server [{}] is refused", information[0]);
        }
    }

    public Database getDatabase() {
        return this.database;
    }

}
