package org.graviton.database.repository;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.graviton.database.AbstractDatabase;
import org.graviton.database.LoginDatabase;
import org.graviton.game.client.account.Account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.graviton.database.jooq.login.tables.Accounts.ACCOUNTS;

/**
 * Created by Botan on 05/11/2016 : 13:14
 */
public class AccountRepository {
    private final Map<Integer, Account> accounts;
    @Inject
    private PlayerRepository playerRepository;
    private LoginDatabase database;

    @Inject
    public AccountRepository(@Named("database.login") AbstractDatabase database) {
        this.accounts = new ConcurrentHashMap<>();
        this.database = (LoginDatabase) database;
    }

    public Account load(int accountId) {
        Account account = new Account(database.getRecord(ACCOUNTS, ACCOUNTS.ID.equal(accountId)), playerRepository);
        this.accounts.put(accountId, account);
        return account;
    }

    public void unload(int accountId) {
        Account account = this.accounts.remove(accountId);
        account.getPlayers().forEach(player -> playerRepository.unload(player.getId()));
    }

    public Account get(int account) {
        return this.accounts.get(account);
    }

    public void updateInformation(Account account) {
        this.database.update(ACCOUNTS).set(ACCOUNTS.LAST_CONNECTION, account.getLastConnection()).
                set(ACCOUNTS.LAST_ADDRESS, account.getLastAddress()).
                where(ACCOUNTS.ID.equal(account.getId())).execute();
    }

}
