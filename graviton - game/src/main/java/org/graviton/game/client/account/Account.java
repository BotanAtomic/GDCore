package org.graviton.game.client.account;

import lombok.Data;
import org.graviton.database.repository.PlayerRepository;
import org.graviton.game.client.player.Player;
import org.graviton.game.trunk.type.Bank;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.PlayerPacketFormatter;
import org.jooq.Record;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.graviton.database.jooq.login.tables.Accounts.ACCOUNTS;

/**
 * Created by Botan on 05/11/2016 : 01:04
 */
@Data
public class Account {
    private final int id;
    private final String question, answer, nickname;

    private byte rights;
    private String channels;
    private String lastConnection, lastAddress;

    private boolean friendNotification;

    private Collection<Player> players;

    private List<Integer> friends, enemies;

    private GameClient client;

    private String cachedPlayerPacket;

    private Bank bank;

    public Account(Record record, PlayerRepository playerRepository) {
        this.id = record.get(ACCOUNTS.ID);
        this.question = record.get(ACCOUNTS.QUESTION);
        this.answer = record.get(ACCOUNTS.ANSWER);
        this.nickname = record.get(ACCOUNTS.NICKNAME);
        this.rights = record.get(ACCOUNTS.RIGHTS);
        this.channels = record.get(ACCOUNTS.CHANNELS);
        this.lastConnection = record.get(ACCOUNTS.LAST_CONNECTION);
        this.lastAddress = record.get(ACCOUNTS.LAST_ADDRESS);
        this.friendNotification = record.get(ACCOUNTS.FRIEND_NOTIFICATION_LISTENER) > 0;
        this.cachedPlayerPacket = PlayerPacketFormatter.playersPacketMessage(this.players = playerRepository.getPlayers(this));
    }

    public String getPlayerPacket(boolean useCache) {
        if (useCache)
            return cachedPlayerPacket;
        return PlayerPacketFormatter.playersPacketMessage(this.players);
    }

    public Player getPlayer(int playerId) {
        Optional<Player> record;
        return (record = this.players.stream().filter(player -> player.getId() == playerId).findFirst()).isPresent() ? record.get() : null;
    }

}
