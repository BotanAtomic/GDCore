package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.npc.Npc;
import org.graviton.game.exchange.Exchange;
import org.graviton.game.exchange.type.NpcExchange;
import org.graviton.game.exchange.type.PlayerExchange;
import org.graviton.game.items.Item;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.ExchangePacketFormatter;
import org.graviton.network.game.protocol.NpcPacketFormatter;

/**
 * Created by Botan on 11/02/2017. 14:25
 */

@Slf4j
public class ExchangeHandler {
    private final GameClient client;

    public ExchangeHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, char subHeader) {
        switch (subHeader) {
            case 'A':
                client.getPlayer().getExchange().accept();
                break;

            case 'B':
                client.getPlayer().getExchange().buy(Integer.parseInt(data.split("\\|")[0]), Short.parseShort(data.split("\\|")[1]));
                break;

            case 'K':
                client.getPlayer().getExchange().toggle(client.getPlayer().getId());
                break;

            case 'M':
                doExchangeAction(data);
                break;

            case 'R':
                request(data);
                break;

            case 'S':
                client.getPlayer().getExchange().sell(Integer.parseInt(data.split("\\|")[0]), Short.parseShort(data.split("\\|")[1]));
                break;

            case 'V':
                client.getPlayer().getExchange().cancel();
                break;

            default:
                log.error("not implemented exchange packet '{}'", subHeader);

        }
    }

    private void request(String packet) {
        String[] data = packet.split("\\|");
        switch (Byte.parseByte(data[0])) {
            case 0: //npc buy
                Npc npc = (Npc) client.getPlayer().getGameMap().getCreature(Integer.parseInt(data[1]));
                client.send(NpcPacketFormatter.buyRequestMessage(npc.getId()));
                client.send(NpcPacketFormatter.itemListMessage(npc.getTemplate().getItems(client.getEntityFactory())));
                client.getPlayer().setExchange(new NpcExchange(npc, client.getPlayer()));
                break;
            case 1: //player
                requestPlayerExchange(Integer.parseInt(data[1]));
                break;
            case 2: //npc exchange
                break;
        }
    }

    private void requestPlayerExchange(int targetId) {
        Player target = client.getEntityFactory().getPlayerRepository().find(targetId);
        if (target.getMap().getId() == client.getPlayer().getMap().getId() && !target.isBusy()) {
            target.send(ExchangePacketFormatter.requestMessage(client.getPlayer().getId(), targetId, (byte) 1));
            client.send(ExchangePacketFormatter.requestMessage(client.getPlayer().getId(), targetId, (byte) 1));
            new PlayerExchange(client.getPlayer(), target);
        } else
            client.send(ExchangePacketFormatter.requestErrorMessage());
    }

    private void doExchangeAction(String packet) {
        Exchange exchange = client.getPlayer().getExchange();

        String[] information = packet.contains("|") ? packet.substring(2).split("\\|") : null;
        switch (packet.charAt(0)) {
            case 'O':
                if (packet.charAt(1) == '+') {
                    int id = Integer.parseInt(information[0]);
                    short quantity = Short.parseShort(information[1]);
                    short quantityInExchange = exchange.getItemQuantity(client.getPlayer().getId(), id);

                    Item object = client.getPlayer().getInventory().get(id);

                    if (object == null || (quantity <= 0))
                        return;

                    if (quantity > object.getQuantity() - quantityInExchange)
                        quantity = (short) (object.getQuantity() - quantityInExchange);

                    exchange.addItem(id, quantity, client.getPlayer().getId());
                } else
                    exchange.removeItem(Integer.parseInt(information[0]), Short.parseShort(information[1]), client.getPlayer().getId());

                break;
            case 'G':// Kamas
                exchange.editKamas(Integer.parseInt(packet.substring(1)), client.getPlayer().getId());
                break;
            case 'R':// Repeat (for job only)
                break;
            case 'r': //stop craft
                break;
        }
    }

}
