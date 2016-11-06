package org.graviton.database.repository;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.graviton.client.account.Account;
import org.graviton.client.player.Player;
import org.graviton.database.AbstractDatabase;
import org.graviton.database.LoginDatabase;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.graviton.database.jooq.login.tables.Players.PLAYERS;

/**
 * Created by Botan on 06/11/2016 : 12:51
 */
public class PlayerRepository {
    private final Map<Integer, Player> players;
    private LoginDatabase database;

    @Inject
    public PlayerRepository(@Named("database.login") AbstractDatabase database) {
        this.players = new ConcurrentHashMap<>();
        this.database = (LoginDatabase) database;
    }

    public Collection<Player> getPlayers(Account account) {
        return database.getResult(PLAYERS, PLAYERS.OWNER.equal(account.getId())).stream().map(record -> {
            Player player = new Player(record, account);
            this.players.put(player.getId(), player);
            return player;
        }).collect(Collectors.toList());
    }

    public void unload(int player) {
        this.players.remove(player);
    }
}
