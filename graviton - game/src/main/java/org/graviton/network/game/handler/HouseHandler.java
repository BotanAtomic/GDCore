package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.client.account.Account;
import org.graviton.game.client.player.Player;
import org.graviton.game.house.House;
import org.graviton.game.trunk.type.Trunk;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.HousePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.graviton.lang.LanguageSentence.BOUGHT_HOUSE;
import static org.graviton.network.game.protocol.HousePacketFormatter.cancelBuyMessage;

/**
 * Created by Botan on 25/03/2017. 13:07
 */

@Slf4j
public class HouseHandler {
    private final GameClient client;

    public HouseHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, char subHeader) {
        switch (subHeader) {
            case 'B':
                buy(Long.parseLong(data));
                break;

            case 'Q':
                kick(Integer.parseInt(data));
                break;

            case 'S':
                sell(Long.parseLong(data));
                break;

            case 'V':
                client.send(cancelBuyMessage());
                break;
            default:
                log.error("not implemented house packet '{}'", subHeader);
        }
    }

    private void kick(int playerId) {
        Player target = client.getEntityFactory().getPlayerRepository().find(playerId);

        if (target != null) {
            House house = client.getInteractionManager().getHouseInteraction();
            target.changeMap(house.getTemplate().getGameMap(), house.getTemplate().getGameCell());
            target.send(MessageFormatter.kickedOfHouseMessage(client.getPlayer().getName()));
        }
    }

    private void sell(long price) {
        House house = client.getInteractionManager().getHouseInteraction();
        house.setPrice(price);
        client.getEntityFactory().updateHouse(house);
        client.getPlayer().getGameMap().send(HousePacketFormatter.singleLoadMessage(new StringBuilder(), house, client.getEntityFactory(), false));
        client.send(cancelBuyMessage());
    }

    private void buy(long price) {
        House house = client.getInteractionManager().getHouseInteraction();

        client.getPlayer().getInventory().addKamas(-price);

        house.setKey("-");
        house.setPrice(0);

        client.send(cancelBuyMessage());
        client.send(PlayerPacketFormatter.asMessage(client.getPlayer()));
        client.getPlayer().getGameMap().send(HousePacketFormatter.singleLoadMessage(new StringBuilder(), house, client.getEntityFactory(), false));
        client.getPlayer().getGameMap().send(HousePacketFormatter.loadPersonalHouse(Collections.singleton(house), false));

        if (house.getOwner() > 0) {
            Account account = client.getAccountRepository().get(house.getOwner());
            if (account != null) {
                account.getClient().send(MessageFormatter.customStaticMessage(client.getLanguage().getSentence(BOUGHT_HOUSE, String.valueOf(price), client.getPlayer().getName())));
                account.getClient().send(HousePacketFormatter.unloadPersonalHouse(house.getTemplate().getId()));
                account.getBank().setKamas(account.getBank().getKamas() + price);
            } else
                (account = client.getAccountRepository().find(house.getOwner())).getBank().setKamas(account.getBank().getKamas() + price);
            client.getAccountRepository().updateBank(account.getBank());
        }

        List<Trunk> trunks = client.getEntityFactory().getTrunks().get(house.getTemplate().getId());
        if (trunks != null) {
            trunks.forEach(trunk -> {
                trunk.setOwner(client.getAccount().getId());
                client.getEntityFactory().getGameMapRepository().updateTrunk(trunk);
            });
        }

        house.setOwner(client.getAccount().getId());
        client.getEntityFactory().updateHouse(house);
    }
}
