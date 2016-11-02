package org.graviton.network.exchange;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;
import org.graviton.database.models.GameServer;
import org.graviton.database.repository.GameServerRepository;
import org.graviton.network.exchange.state.State;
import org.graviton.network.login.LoginServer;
import org.graviton.network.login.protocol.LoginProtocol;
import org.graviton.protocol.ExchangeProtocol;
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
     * Message form : #args#data
     *
     * @param packet Data reception
     */
    public void handle(String packet) {
        switch (packet.substring(0, 1)) {
            case "I":
                gameServerRepository.setGameServerInformations(packet.substring(2), this);
                setState((byte) 1);
                log.debug("Game server [{}] is successfully connected");
                break;
            case "S":
                setState(Byte.parseByte(packet.substring(2)));
                break;
        }
    }

    public void send(String data) {
        session.write(StringUtils.stringToBuffer(data));
    }

    private void setState(byte id) {
        gameServer.setState(State.values()[id - 1]);
        String serversData = LoginProtocol.serversInformationsMessage(gameServerRepository.getGameServers().values());
        loginServer.getClients().forEach(client -> client.send(serversData));
    }

}
