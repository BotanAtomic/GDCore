package org.graviton.client;

import lombok.Data;
import org.jooq.Record;

import static org.graviton.database.jooq.login.tables.Accounts.ACCOUNTS;

/**
 * Created by Botan on 05/11/2016 : 01:04
 */
@Data
public class Account {
    private final int id;
    private final String question, answer;

    private byte rights;
    private String channels;
    private String lastConnection, lastAddress;

    private boolean friendNotification;

    public Account(Record record) {
        this.id = record.get(ACCOUNTS.ID);
        this.question = record.get(ACCOUNTS.QUESTION);
        this.answer = record.get(ACCOUNTS.ANSWER);
        this.rights = record.get(ACCOUNTS.RIGHTS);
        this.channels = record.get(ACCOUNTS.CHANNELS);
        this.lastConnection = record.get(ACCOUNTS.LAST_CONNECTION);
        this.lastAddress = record.get(ACCOUNTS.LAST_ADDRESS);
        this.friendNotification = record.get(ACCOUNTS.FRIEND_NOTIFICATION_LISTENER) > 0;
    }
}
