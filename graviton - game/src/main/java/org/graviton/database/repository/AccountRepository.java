package org.graviton.database.repository;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.graviton.client.Account;
import org.graviton.database.LoginDatabase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.graviton.database.jooq.login.tables.Accounts.ACCOUNTS;

/**
 * Created by Botan on 05/11/2016 : 13:14
 */
public class AccountRepository {
    private final Map<Integer, Account> accounts;
    private LoginDatabase database;

    @Inject
    public AccountRepository(@Named("database.login") LoginDatabase database) {
        this.accounts = new ConcurrentHashMap<>();
        this.database = database;
    }

    public void load(int account) {
        this.accounts.put(account, new Account(database.getRecord(ACCOUNTS, ACCOUNTS.ID.equal(account))));
    }

}
