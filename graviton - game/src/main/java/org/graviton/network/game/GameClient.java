package org.graviton.network.game;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Data;
import org.apache.mina.core.session.IoSession;
import org.graviton.api.Language;
import org.graviton.client.account.Account;
import org.graviton.client.player.Player;
import org.graviton.database.entity.EntityFactory;
import org.graviton.database.repository.AccountRepository;
import org.graviton.database.repository.PlayerRepository;
import org.graviton.network.game.handler.MessageHandler;
import org.graviton.network.game.protocol.GameProtocol;
import org.graviton.network.game.protocol.PlayerProtocol;

/**
 * Created by Botan on 04/11/2016 : 22:50
 */
@Data
public class GameClient {
    private final MessageHandler messageHandler = new MessageHandler(this);
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

    public void createPlayer(String data) {
        Player player = new Player(playerRepository.getNextId(), data, this.account, entityFactory);
        this.account.getPlayers().add(player);
        this.send(this.account.getPlayerPacket(false));
        playerRepository.create(player);
    }

    public void selectPlayer(int playerId) {
        Player player = account.getPlayer(playerId);
        this.player = player;
        send(PlayerProtocol.getASKMessage(player));
    }

    public void createGame() {
        send(GameProtocol.gameCreationSuccessMessage());
        send(PlayerProtocol.getAsMessage(player, entityFactory.getExperience(player.getLevel()), player.getAlignement(), player.getStatistics()));
    }

    public void deletePlayer(int player, String secretAnswer) {
        if (secretAnswer.isEmpty() || secretAnswer.equals(account.getAnswer())) {
            playerRepository.remove(account.getPlayer(player));
            send(account.getPlayerPacket(false));
        } else
            send(GameProtocol.playerDeleteFailedMessage());
    }

}
