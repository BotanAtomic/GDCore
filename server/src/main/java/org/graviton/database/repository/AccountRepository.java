package org.graviton.database.repository;

import com.google.inject.Inject;
import org.graviton.database.LoginDatabase;
import org.graviton.database.models.Account;
import org.graviton.database.models.Player;
import org.graviton.network.login.protocol.LoginProtocol;
import org.jooq.Record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.graviton.database.jooq.tables.Accounts.ACCOUNTS;
import static org.graviton.database.jooq.tables.Players.PLAYERS;


/**
 * Created by Botan on 30/10/2016 : 00:24
 */

public class AccountRepository {
    private final Map<Integer, Account> accounts;
    @Inject
    private LoginDatabase database;

    public AccountRepository() {
        this.accounts = new ConcurrentHashMap<>();
    }

    public Account load(String name) {
        Record record = database.getRecord(ACCOUNTS, ACCOUNTS.NAME.equal(name));

        if (record == null)
            return null;

        if (accounts.containsKey(record.get(ACCOUNTS.ID)))
            accounts.get(record.get(ACCOUNTS.ID)).getClient().send(LoginProtocol.alreadyConnected());

        return new Account(record);
    }

    public void register(Account account) {
        this.accounts.put(account.getId(), account);
    }

    public void unregister(int id) {
        this.accounts.remove(id);
    }

    public void updateNickname(Account account) {
        database.getDslContext().update(ACCOUNTS).set(ACCOUNTS.NICKNAME, account.getNickname()).where(ACCOUNTS.ID.equal(account.getId())).execute();
    }

    public boolean isAvailableNickname(String nickname) {
        return database.getRecord(ACCOUNTS, ACCOUNTS.NICKNAME.equal(nickname)) == null;
    }

    public Collection<Player> load(int accountId) {
        return database.getResult(PLAYERS, PLAYERS.OWNER.equal(accountId)).stream().map(record -> new Player(record.getValue(PLAYERS.ID), record.getValue(PLAYERS.SERVER))).collect(Collectors.toList());
    }

    public Collection<Player> getPlayers(String nickname) {
        Record result = database.getRecord(ACCOUNTS, ACCOUNTS.NICKNAME.equal(nickname));
        return result != null ? database.getResult(PLAYERS, PLAYERS.OWNER.equal(result.getValue(ACCOUNTS.ID))).stream().map(record -> new Player(record.getValue(PLAYERS.ID), record.getValue(PLAYERS.SERVER))).collect(Collectors.toList()) : new ArrayList<>();
    }

}
