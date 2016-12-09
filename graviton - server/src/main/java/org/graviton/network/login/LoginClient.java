package org.graviton.network.login;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Data;
import org.apache.mina.core.session.IoSession;
import org.graviton.api.AbstractHandler;
import org.graviton.database.LoginDatabase;
import org.graviton.database.models.Account;
import org.graviton.database.repository.AccountRepository;
import org.graviton.database.repository.GameServerRepository;
import org.graviton.network.login.handler.VersionHandler;
import org.graviton.network.login.protocol.LoginProtocol;
import org.graviton.utils.Utils;


/**
 * Created by Botan on 29/10/2016 : 22:56
 */
@Data
public class LoginClient {
    private final IoSession session;
    private final String key;
    @Inject
    private LoginDatabase database;
    @Inject
    private AccountRepository accountRepository;
    @Inject
    private GameServerRepository gameServerRepository;
    private AbstractHandler handler;

    private Account account;

    public LoginClient(IoSession session, Injector injector) {
        injector.injectMembers(this);
        this.session = session;
        this.session.write(LoginProtocol.helloConnect(this.key = Utils.generateKey()));
        this.handler = new VersionHandler();
    }

    public void attachAccount(Account account) {
        this.account = account;
        account.setClient(this);
    }

    public void send(String data) {
        this.session.write(data);
    }

    public void handle(String data) {
        this.handler.handle(data, this);
    }

    public void disconnect() {
        this.session.closeNow();
    }

}
