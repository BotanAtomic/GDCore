package org.graviton.database.repository;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.graviton.database.AbstractDatabase;
import org.graviton.database.LoginDatabase;
import org.graviton.database.Repository;
import org.graviton.game.client.account.Account;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.graviton.database.jooq.login.tables.Accounts.ACCOUNTS;

/**
 * Created by Botan on 05/11/2016 : 13:14
 */
public class AccountRepository extends Repository<Integer, Account> {
    @Inject
    private PlayerRepository playerRepository;

    private LoginDatabase database;

    @Inject
    public AccountRepository(@Named("database.login") AbstractDatabase database) {
        this.database = (LoginDatabase) database;
    }

    public void unload(int accountId) {
        Account account = super.remove(accountId);
        account.setLastConnection(new SimpleDateFormat("yyyy~MM~dd~HH~mm").format(new Date()));
        updateInformation(account);
        account.getPlayers().forEach(player -> playerRepository.unload(player.getId()));
    }

    public Account load(int accountId) {
        Account account = new Account(database.getRecord(ACCOUNTS, ACCOUNTS.ID.equal(accountId)), playerRepository);
        super.add(accountId, account);
        return account;
    }

    private void updateInformation(Account account) {
        this.database.update(ACCOUNTS).set(ACCOUNTS.LAST_CONNECTION, account.getLastConnection()).set(ACCOUNTS.LAST_ADDRESS, account.getLastAddress()).where(ACCOUNTS.ID.equal(account.getId())).execute();
    }

    @Override
    public Account find(Object value) {
        Account account = super.get((int) value);
        return account == null ? load((int) value) : account;
    }
}
