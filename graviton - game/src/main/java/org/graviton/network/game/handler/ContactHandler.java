package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 22/03/2017. 12:08
 */

@Slf4j
public class ContactHandler {
    private final GameClient client;
    private final ContactType contactType;

    public ContactHandler(GameClient client, boolean friend) {
        this.client = client;
        this.contactType = friend ? ContactType.FRIEND : ContactType.ENEMY;
    }

    public void handle(String data, char subHeader) {
        switch (subHeader) {
            case 'A':
                addContact(data);
                break;

            case 'L':
                client.send(getList());
                break;
            default:
                log.error("not implemented contact packet '{}'", subHeader);
        }
    }

    private void addContact(String name) {

    }

    private String getList() {
        return "";
    }

    enum ContactType {
        FRIEND('F'),
        ENEMY('i');

        private final char value;

        ContactType(char value) {
            this.value = value;
        }

    }

}
