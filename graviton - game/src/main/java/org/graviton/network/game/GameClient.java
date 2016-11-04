package org.graviton.network.game;

import com.google.inject.Injector;
import org.apache.mina.core.session.IoSession;
import org.graviton.network.game.protocol.GameProtocol;

/**
 * Created by Botan on 04/11/2016 : 22:50
 */
public class GameClient {
    private final long id;
    private final IoSession session;

    public GameClient(IoSession session, Injector injector) {
        injector.injectMembers(this);
        this.id = session.getId();
        this.session = session;
        send(GameProtocol.helloGameMessage());
    }

    public void send(String data) {
        session.write(data);
    }

    public void handle(String data) {

    }

    public void disconnect() {
        this.session.closeNow();
    }

}
