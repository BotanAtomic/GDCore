package org.graviton.game.trunk.type;

import lombok.Data;
import org.graviton.database.repository.PlayerRepository;
import org.graviton.game.client.account.Account;
import org.graviton.game.trunk.AbstractTrunk;
import org.jooq.Record;

import java.util.stream.Stream;

import static org.graviton.database.jooq.game.tables.Banks.BANKS;

/**
 * Created by Botan on 13/05/17. 17:29
 */

@Data
public class Bank extends AbstractTrunk {
    private final Account account;

    public Bank(Record record, Account account, PlayerRepository playerRepository) {
        super(record.get(BANKS.KAMAS));
        this.account = account;
        Stream.of(record.get(BANKS.ITEMS).split(";")).filter(data -> !data.isEmpty()).forEach(data -> super.add(playerRepository.loadItem(Integer.parseInt(data))));
    }

    public int getCost() {
        return super.size();
    }


}
