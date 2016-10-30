package org.graviton.database.repository;

import com.google.inject.Inject;
import org.graviton.database.LoginDatabase;
import org.graviton.database.models.Account;
import org.jooq.Record;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.graviton.database.jooq.tables.Accounts.ACCOUNTS;

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
        return record != null ? new Account(record) : null;
    }

    public void register(Account account) {
        this.accounts.put(account.getId(), account);
    }

    public void updateNickname(Account account) {
        database.getDslContext().update(ACCOUNTS).set(ACCOUNTS.NICKNAME, account.getNickname()).where(ACCOUNTS.ID.equal(account.getId())).execute();
    }

}
