package org.graviton.database.models;

import lombok.Data;
import org.jooq.Record;

import static org.graviton.database.jooq.tables.Accounts.ACCOUNTS;

/**
 * Created by Botan on 30/10/2016 : 00:17
 */

@Data
public class Account {
    private final int id;
    private final String name;
    private final String password;
    private final String nickname;
    private final long banned;

    public Account(Record record) {
        this.id = record.get(ACCOUNTS.ID);
        this.name = record.get(ACCOUNTS.NAME);
        this.password = record.get(ACCOUNTS.PASSWORD);
        this.nickname = record.get(ACCOUNTS.NICKNAME);
        this.banned = record.get(ACCOUNTS.BANNED);
    }
}
