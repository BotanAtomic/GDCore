package org.graviton.network.exchange;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;
import org.graviton.database.models.GameServer;
import org.graviton.database.repository.GameServerRepository;
import org.graviton.network.exchange.protocol.ExchangeProtocol;
import org.graviton.utils.StringUtils;

/**
 * Created by Botan on 30/10/2016 : 13:46
 */

@Slf4j
public class ExchangeClient {
    private final IoSession session;
    @Inject
    private GameServerRepository gameServerRepository;
    @Getter
    @Setter
    private GameServer gameServer;

    public ExchangeClient(IoSession session, Injector injector) {
        injector.injectMembers(this);
        this.session = session;
        send(ExchangeProtocol.needInformations());
    }

    public void handle(String packet) {
        switch (packet.substring(0, 2)) {
            case "SI":
                gameServerRepository.setGameServerInformations(packet.substring(2), this);
                log.debug("Game server ");
                break;
        }
    }

    public void send(String data) {
        session.write(StringUtils.stringToBuffer(data));
    }

}
