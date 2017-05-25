package org.graviton.network.game;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Data;
import org.apache.mina.core.session.IoSession;
import org.graviton.lang.Language;
import org.graviton.database.entity.EntityFactory;
import org.graviton.database.repository.AccountRepository;
import org.graviton.database.repository.PlayerRepository;
import org.graviton.game.client.account.Account;
import org.graviton.game.client.player.Player;
import org.graviton.game.guild.GuildMember;
import org.graviton.game.interaction.InteractionManager;
import org.graviton.network.game.handler.base.MessageHandler;
import org.graviton.network.game.protocol.GamePacketFormatter;

import java.util.Date;

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

    @Inject private AccountRepository accountRepository;
    @Inject private PlayerRepository playerRepository;
    @Inject private EntityFactory entityFactory;

    private boolean disconnected = false;


    public GameClient(IoSession session, Injector injector) {
        injector.injectMembers(this);
        this.id = session.getId();
        this.session = session;
        send(GamePacketFormatter.helloGameMessage());
    }

    public void send(String data) {
        session.write(data);
    }

    public void sendFormat(String data, String regex) {
        for (String packet : data.split(regex))
            session.write(packet);
    }


    public void disconnect() {
        if(disconnected)
            return;

        if (player != null) {
            if (this.player.getFight() != null) {
                if (player.getFight().allowDisconnection())
                    this.player.getFight().disconnect(player);
                else
                    this.player.getFight().quit(player);
            }

            if (this.player.getGuild() != null) {
                GuildMember guildMember = player.getGuild().getMember(player.getId());
                guildMember.setLastConnection(new Date());
                guildMember.setPlayer(null);
                entityFactory.getGuildRepository().updateGuildMember(guildMember);
            }

            if(player.getExchange() != null)
                player.getExchange().cancel();

            if (player.getFight() == null || player.getFight() != null && !player.getFight().allowDisconnection())
                this.player.getMap().out(player);
        }
        this.playerRepository.save(account);
        this.accountRepository.unload(account.getId());
        this.session.closeNow();
        disconnected = true;
    }

    public void setLanguage(String language) {
        this.language = Language.get(language);
    }

    public MessageHandler getBaseHandler() {
        return ((MessageHandler) session.getAttribute((byte) 1));
    }

    public void setEndFight() {
        getBaseHandler().getGameHandler().setEndFight();
    }

}
