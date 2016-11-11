package org.graviton.database.repository;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.graviton.client.account.Account;
import org.graviton.client.player.Player;
import org.graviton.database.AbstractDatabase;
import org.graviton.database.LoginDatabase;
import org.graviton.network.exchange.ExchangeConnector;
import org.graviton.utils.StringUtils;

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

    public void create(Player player) {
        database.getDslContext().
                insertInto(PLAYERS, PLAYERS.ID, PLAYERS.OWNER, PLAYERS.NAME, PLAYERS.BREED, PLAYERS.SEX, PLAYERS.SKIN, PLAYERS.COLORS, PLAYERS.SERVER).
                values(player.getId(), player.getAccount().getId(), player.getName(), player.getBreed().id(),
                        player.getSex(), player.getSkin(), StringUtils.parseColors(player.getColors()), (byte) ExchangeConnector.serverId).execute();
    }

    public void remove(Player player) {
        database.getDslContext().delete(PLAYERS).where(PLAYERS.ID.equal(player.getId())).execute();
        player.getAccount().getPlayers().remove(player);
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

    public int getNextId() {
        return database.getNextId(PLAYERS, PLAYERS.ID);
    }
}
