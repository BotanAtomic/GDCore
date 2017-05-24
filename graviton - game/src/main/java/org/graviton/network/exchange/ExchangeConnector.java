package org.graviton.network.exchange;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.graviton.api.InjectSetting;
import org.graviton.api.Manageable;
import org.graviton.core.Program;
import org.graviton.database.repository.AccountRepository;
import org.graviton.game.client.account.Account;
import org.graviton.network.exchange.protocol.ExchangeProtocol;
import org.graviton.utils.Utils;

import java.net.InetSocketAddress;

/**
 * Created by Botan on 04/11/2016 : 22:50
 */
@Slf4j
public class ExchangeConnector implements IoHandler, Manageable {
    @InjectSetting("server.id")
    public static int serverId;

    private final NioSocketConnector socketConnector;

    @Inject private AccountRepository accountRepository;

    private IoSession session;

    @InjectSetting("exchange.ip") private String exchangeAddress;
    @InjectSetting("exchange.port") private int exchangePort;
    @InjectSetting("server.ip") private String address;
    @InjectSetting("server.port") private int port;
    @InjectSetting("server.key") private String serverKey;

    @Inject public ExchangeConnector(Program program) {
        program.register(this);
        this.socketConnector = new NioSocketConnector();
        this.socketConnector.setHandler(this);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {

    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.debug("Exchange server was disconnected");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        handle(Utils.bufferToString(message));
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        session.closeNow();
    }

    private void send(String data) {
        session.write(Utils.stringToBuffer(data));
        log.debug("send > {}", data);
    }

    private void handle(String data) {
        log.debug("receive < {}", data);
        switch (data.charAt(0)) {
            case 'S':
                if (data.charAt(1) == 'A')
                    log.debug("Login server : connection accepted");
                else {
                    log.debug("Login server : connection refused");
                    System.exit(0);
                }
                break;
            case '-':
                Account account = this.accountRepository.get(Integer.parseInt(data.substring(1)));
                if (account != null && account.getClient() != null)
                    account.getClient().send("AlEa");

                break;
            case '+':
                this.accountRepository.load(Integer.parseInt(data.substring(1)));
                break;
        }
    }

    @Override
    public void start() {
        ConnectFuture future = socketConnector.connect(new InetSocketAddress(exchangeAddress, exchangePort));
        future.awaitUninterruptibly();
        this.session = future.getSession();

        if (this.session != null) {
            log.info("Connected to the exchange server {{}/{}}", exchangeAddress, exchangePort);
            send(ExchangeProtocol.informationMessage((byte) serverId, this.serverKey, this.address, this.port));
         } else
            log.info("Unable to connect to the exchange server {{}/{}}", exchangeAddress, exchangePort);

    }

    @Override
    public void stop() {
        session.closeNow();
        socketConnector.dispose();
        log.debug("Exchange connector was successfully disconnected ");
    }

    @Override public byte index() {
        return 3;
    }
}
