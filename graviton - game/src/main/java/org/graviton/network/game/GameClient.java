package org.graviton.network.game;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Data;
import org.apache.mina.core.session.IoSession;
import org.graviton.api.Language;
import org.graviton.database.entity.EntityFactory;
import org.graviton.database.repository.AccountRepository;
import org.graviton.database.repository.PlayerRepository;
import org.graviton.game.client.account.Account;
import org.graviton.game.client.player.Player;
import org.graviton.game.interaction.InteractionManager;
import org.graviton.network.game.handler.base.MessageHandler;
import org.graviton.network.game.protocol.GamePacketFormatter;

/**
 * Created by Botan on 04/11/2016 : 22:50
 */
@Data
public class GameClient {
    private final InteractionManager interactionManager = new InteractionManager(this);

    private final long id;
    private final IoSession session;

    private Account account;
    private Player player;
    private Language language;

    @Inject
    private AccountRepository accountRepository;
    @Inject
    private PlayerRepository playerRepository;
    @Inject
    private EntityFactory entityFactory;


    public GameClient(IoSession session, Injector injector) {
        injector.injectMembers(this);
        this.id = session.getId();
        this.session = session;
        send(GamePacketFormatter.helloGameMessage());
    }

    public void send(String data) {
        session.write(data);
    }

    void disconnect() {
        if (player != null)
            this.player.getGameMap().out(player);

        this.playerRepository.save(account);
        this.accountRepository.unload(account.getId());
        this.session.closeNow();
    }

    public void setLanguage(String language) {
        this.language = Language.get(language);
    }

    public final MessageHandler getBaseHandler() {
        return ((MessageHandler) session.getAttribute((byte) 1));
    }

}
