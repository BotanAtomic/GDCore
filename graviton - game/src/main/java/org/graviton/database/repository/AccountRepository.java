package org.graviton.database.repository;

import com.google.inject.Inject;
import org.graviton.database.Database;
import org.graviton.database.Repository;
import org.graviton.database.api.LoginDatabase;
import org.graviton.game.client.account.Account;
import org.graviton.game.trunk.type.Bank;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.graviton.database.jooq.login.tables.Accounts.ACCOUNTS;
import static org.graviton.database.jooq.game.tables.Banks.BANKS;


/**
 * Created by Botan on 05/11/2016 : 13:14
 */
public class AccountRepository extends Repository<Integer, Account> {
    @Inject private PlayerRepository playerRepository;

    private Database database;

    @Inject
    public AccountRepository(@LoginDatabase Database database) {
        this.database = database;
    }

    public void unload(int accountId) {
        Account account = super.remove(accountId);
        account.setLastConnection(new SimpleDateFormat("yyyy~MM~dd~HH~mm").format(new Date()));
        updateInformation(account);
        account.getPlayers().forEach(player -> playerRepository.unload(player.getId()));
    }

    public Account load(int accountId) {
        Account account = new Account(database.getRecord(ACCOUNTS, ACCOUNTS.ID.equal(accountId)), playerRepository);
        account.setBank(new Bank(database.getRecord(BANKS, BANKS.OWNER.equal(account.getId())), account, playerRepository));
        super.add(accountId, account);
        return account;
    }

    private void updateInformation(Account account) {
        this.database.update(ACCOUNTS).set(ACCOUNTS.LAST_CONNECTION, account.getLastConnection()).set(ACCOUNTS.LAST_ADDRESS, account.getLastAddress()).where(ACCOUNTS.ID.equal(account.getId())).execute();
    }

    public void updateBank(Bank bank) {
        this.database.update(BANKS).set(BANKS.ITEMS, bank.compileItems()).set(BANKS.KAMAS, bank.getKamas()).where(BANKS.OWNER.equal(bank.getAccount().getId())).execute();
    }

    @Override
    public Account find(Object value) {
        Account account = super.get((int) value);
        return account == null ? load((int) value) : account;
    }
}
