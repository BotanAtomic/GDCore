package org.graviton.network.game;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Data;
import org.apache.mina.core.session.IoSession;
import org.graviton.api.Language;
import org.graviton.client.account.Account;
import org.graviton.database.repository.AccountRepository;
import org.graviton.network.game.handler.MessageHandler;
import org.graviton.network.game.protocol.GameProtocol;

/**
 * Created by Botan on 04/11/2016 : 22:50
 */
@Data
public class GameClient {
    private final MessageHandler messageHandler = new MessageHandler(this);
    private final long id;
    private final IoSession session;
    @Inject
    private AccountRepository accountRepository;
    private Account account;
    private Language language;

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
        messageHandler.handle(data);
    }

    public void disconnect() {
        this.accountRepository.unload(account.getId());
        this.session.closeNow();
    }

    public void applyTicket(int accountId) {
        Account account = this.accountRepository.load(accountId);
        if (account != null) {
            account.setClient(this);
            this.account = account;
            send(GameProtocol.accountTicketSuccessMessage("0"));
        } else {
            send(GameProtocol.accountTicketErrorMessage());
        }
    }

    public void setLanguage(String language) {
        this.language = Language.get(language);
    }

}
