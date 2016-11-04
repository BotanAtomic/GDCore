package org.graviton.network.exchange;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;
import org.graviton.database.models.GameServer;
import org.graviton.database.repository.GameServerRepository;
import org.graviton.network.exchange.protocol.ExchangeProtocol;
import org.graviton.network.exchange.state.State;
import org.graviton.network.login.LoginServer;
import org.graviton.network.login.protocol.LoginProtocol;
import org.graviton.utils.StringUtils;

/**
 * Created by Botan on 30/10/2016 : 13:46
 */

@Slf4j
public class ExchangeClient {
    private final IoSession session;
    @Inject
    private GameServerRepository gameServerRepository;
    @Inject
    private LoginServer loginServer;
    @Setter
    private GameServer gameServer;

    public ExchangeClient(IoSession session, Injector injector) {
        injector.injectMembers(this);
        this.session = session;
        send(ExchangeProtocol.needInformations());
    }


    /**
     * @param packet form :#args#data
     */
    public void handle(String packet) {
        switch (packet.substring(0, 1)) {
            case "I":
                gameServerRepository.setGameServerInformations(packet.substring(1), this);
                break;
            case "S":
                setState(State.get(Byte.parseByte(packet.substring(1))));
                break;
        }
        log.info("[Session {}] receive < {}", session.getId(), packet);
    }

    public void send(String data) {
        session.write(StringUtils.stringToBuffer(data));
        log.info("[Session {}] send > {}", session.getId(), data);
    }

    public void setState(State state) {
        gameServer.setState(state);
        String serversData = LoginProtocol.serversInformationsMessage(gameServerRepository.getGameServers().values());
        loginServer.getClients().forEach(client -> client.send(serversData));
    }

}
