package org.graviton.network.login;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.graviton.api.InjectSetting;
import org.graviton.api.Manageable;
import org.graviton.core.Program;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by Botan on 29/10/2016 : 07:00
 */
@Slf4j
public class LoginServer implements IoHandler, Manageable {
    @InjectSetting("client.version")
    public static String DOFUS_VERSION;
    @InjectSetting("word.forbidden")
    public static String FORBIDDEN_WORD;

    private final NioSocketAcceptor socketAcceptor;
    @Inject
    private Injector injector;
    @InjectSetting("server.port")
    private int port;

    @Inject
    public LoginServer(Program program) throws IOException {
        program.register(this);
        this.socketAcceptor = new NioSocketAcceptor();
        this.socketAcceptor.setReuseAddress(true);
        //this.socketAcceptor.getFilterChain().addFirst("security", new SecurityFilter((byte) 3));
        this.socketAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF8"), LineDelimiter.NUL, new LineDelimiter("\n\0"))));
        this.socketAcceptor.setHandler(this);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        log.debug("[Session {}] created", session.getId());
    }

    @Override
    public void sessionOpened(IoSession session) {
        session.setAttribute("client", new LoginClient(session, injector));
        log.debug("[Session {}] opened", session.getId());
    }

    @Override
    public void sessionClosed(IoSession session) {
        ((LoginClient) session.getAttribute("client")).disconnect();
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
        log.info("[Session {}] receives < {}", session.getId(), message);
        ((LoginClient) session.getAttribute("client")).handle(message.toString());
    }

    @Override
    public void messageSent(IoSession session, Object message) {
        log.info("[Session {}] send > {}", session.getId(), message);

    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        session.closeNow();
        log.debug("[Session {}] input closed", session.getId());
    }

    @Override
    public void start() {
        try {
            this.socketAcceptor.bind(new InetSocketAddress(port));
            log.debug("Login server was successfully bind on port {}", port);
        } catch (IOException e) {
            log.error("Unable to bind the port {} [cause : {}]", port, e.getMessage());
        }
    }

    @Override
    public void stop() {
        this.socketAcceptor.unbind();
        log.debug("Login server was successfully unbind on port {} ", port);
    }

    public Collection<LoginClient> getClients() {
        return this.socketAcceptor.getManagedSessions().values().stream().map(session -> ((LoginClient) session.getAttribute("client"))).collect(Collectors.toList());
    }
}
