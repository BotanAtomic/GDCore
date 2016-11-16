package org.graviton.game.client.account;

import lombok.Data;
import org.graviton.database.repository.PlayerRepository;
import org.graviton.game.client.player.Player;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.PlayerProtocol;
import org.jooq.Record;

import java.util.Collection;

import static org.graviton.database.jooq.login.tables.Accounts.ACCOUNTS;

/**
 * Created by Botan on 05/11/2016 : 01:04
 */
@Data
public class Account {
    private final int id;
    private final String question, answer;

    private byte rights;
    private String channels;
    private String lastConnection, lastAddress;

    private boolean friendNotification;

    private Collection<Player> players;

    private GameClient client;

    private String cachedPlayerPacket;

    public Account(Record record, PlayerRepository playerRepository) {
        this.id = record.get(ACCOUNTS.ID);
        this.question = record.get(ACCOUNTS.QUESTION);
        this.answer = record.get(ACCOUNTS.ANSWER);
        this.rights = record.get(ACCOUNTS.RIGHTS);
        this.channels = record.get(ACCOUNTS.CHANNELS);
        this.lastConnection = record.get(ACCOUNTS.LAST_CONNECTION);
        this.lastAddress = record.get(ACCOUNTS.LAST_ADDRESS);
        this.friendNotification = record.get(ACCOUNTS.FRIEND_NOTIFICATION_LISTENER) > 0;
        this.cachedPlayerPacket = PlayerProtocol.playersPacketMessage(this.players = playerRepository.getPlayers(this));
    }

    public String getPlayerPacket(boolean useCache) {
        if (useCache)
            return cachedPlayerPacket;
        return PlayerProtocol.playersPacketMessage(this.players);
    }

    public Player getPlayer(int playerId) {
        return this.players.stream().filter(player -> player.getId() == playerId).findFirst().get();
    }

}
