package org.graviton.network.exchange;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.graviton.api.InjectSetting;
import org.graviton.api.Manageable;
import org.graviton.core.Program;
import org.graviton.database.repository.GameServerRepository;
import org.graviton.network.exchange.state.State;
import org.graviton.utils.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Botan on 29/10/2016 : 06:59
 */

@Slf4j
public class ExchangeServer implements IoHandler, Manageable {
    private final NioSocketAcceptor socketAcceptor;
    @Inject
    private Injector injector;
    @Inject
    private GameServerRepository gameServerRepository;
    @InjectSetting("exchange.port")
    private int port;

    @Inject
    public ExchangeServer(Program program) {
        program.register(this);
        this.socketAcceptor = new NioSocketAcceptor();
        this.socketAcceptor.setReuseAddress(true);
        this.socketAcceptor.setHandler(this);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        session.setAttribute("client", new ExchangeClient(session, injector));
        log.debug("[Session {}] created", session.getId());
    }

    @Override
    public void sessionOpened(IoSession session) {
        log.debug("[Session {}] opened", session.getId());
    }

    @Override
    public void sessionClosed(IoSession session) {
        ((ExchangeClient) session.getAttribute("client")).setState(State.OFFLINE);
        log.debug("[Session {}] closed", session.getId());
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        log.debug("[Session {}] idle", session.getId());
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        log.error("[Session {}] exception > \n", session.getId(), cause);

    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        ((ExchangeClient) session.getAttribute("client")).handle(Utils.bufferToString(message));
    }

    @Override
    public void messageSent(IoSession session, Object message) {

    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        session.closeNow();
        log.debug("[Session {}] input closed", session.getId());
    }

    @Override
    public void start() {
        long instant = System.currentTimeMillis();
        while(gameServerRepository.getDatabase().getDslContext() == null && (System.currentTimeMillis() - instant) < 5000);

        gameServerRepository.loadGameServers();
        try {
            this.socketAcceptor.bind(new InetSocketAddress(port));
            log.debug("listening on port {}", port);
        } catch (IOException e) {
            log.error("Unable to bind the port {} [cause : {}]", port, e.getMessage());
        }
    }

    @Override
    public void stop() {
        this.socketAcceptor.unbind();
        log.debug("unbind on port {} ", port);
    }

    @Override public byte index() {
        return 2;
    }
}

