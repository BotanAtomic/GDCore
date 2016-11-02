package org.graviton.database.models;

import lombok.Data;
import org.graviton.network.login.LoginClient;
import org.jooq.Record;

import java.util.Collection;

import static org.graviton.database.jooq.login.tables.Accounts.ACCOUNTS;

/**
 * Created by Botan on 30/10/2016 : 00:17
 */

@Data
public class Account {
    private final int id;
    private final String name, password, secretQuestion;
    private final byte rights;

    private boolean banned;
    private String nickname;

    private LoginClient client;

    private Collection<Player> players;

    public Account(Record record) {
        this.id = record.get(ACCOUNTS.ID);
        this.name = record.get(ACCOUNTS.NAME);
        this.password = record.get(ACCOUNTS.PASSWORD);
        this.nickname = record.get(ACCOUNTS.NICKNAME);
        this.secretQuestion = record.get(ACCOUNTS.QUESTION);
        this.banned = record.get(ACCOUNTS.BANNED) == 1;
        this.rights = record.get(ACCOUNTS.RIGHTS);
    }
}
