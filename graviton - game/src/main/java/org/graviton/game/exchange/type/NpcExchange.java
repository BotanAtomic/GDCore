package org.graviton.game.exchange.type;

import org.graviton.game.client.player.Player;
import org.graviton.game.creature.npc.Npc;
import org.graviton.game.exchange.Exchange;
import org.graviton.game.interaction.Status;
import org.graviton.game.items.Item;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.network.game.protocol.ExchangePacketFormatter;
import org.graviton.network.game.protocol.ItemPacketFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

/**
 * Created by Botan on 19/03/2017. 15:07
 */
public class NpcExchange implements Exchange {
    private final Npc npc;
    private final Player player;

    public NpcExchange(Npc npc, Player player) {
        this.npc = npc;
        this.player = player;
        player.setStatus(Status.EXCHANGE);
    }

    @Override
    public void accept() {

    }

    @Override
    public void cancel() {
        player.send(ExchangePacketFormatter.cancelMessage());
        player.update();
        player.setStatus(Status.DEFAULT);
        player.setExchange(null);
    }

    @Override
    public short getItemQuantity(int exchangerId, int itemId) {
        return 0;
    }

    @Override
    public void addItem(int itemId, short quantity, int exchangerId) {

    }

    @Override
    public void removeItem(int itemId, short quantity, int exchangerId) {

    }

    @Override
    public void editKamas(int quantity, int exchangerId) {

    }

    @Override
    public void toggle(int exchangerId) {

    }

    @Override
    public void buy(int itemId, short quantity) {
        ItemTemplate itemTemplate = npc.getTemplate().getItemTemplate(itemId);

        if (player.getInventory().getKamas() >= itemTemplate.getPrice()) {
            Item newItem = itemTemplate.createRandom(player.getEntityFactory());
            newItem.setQuantity(quantity);

            if (player.getInventory().addItem(newItem, true) == null)
                player.send(ItemPacketFormatter.addItemMessage(newItem));

            player.getInventory().addKamas(-itemTemplate.getPrice() * quantity);
            player.send(PlayerPacketFormatter.asMessage(player));
            player.send(ExchangePacketFormatter.buySuccessMessage());
            player.send(PlayerPacketFormatter.podsMessage(player.getStatistics().refreshPods()));
        } else {
            player.send(ExchangePacketFormatter.buyErrorMessage());
        }

    }

    @Override
    public void sell(int itemId, short quantity) {
        Item item = player.getInventory().get(itemId);

        if (item.getQuantity() - quantity <= 0) {
            player.removeItem(item);
            player.send(ItemPacketFormatter.deleteMessage(itemId));
        } else {
            item.changeQuantity((short) -quantity);
            player.send(ItemPacketFormatter.quantityMessage(itemId, item.getQuantity()));
        }

        player.getInventory().addKamas(((item.getTemplate().getPrice() * quantity) / 10));
        player.send(PlayerPacketFormatter.asMessage(player));
        player.send(ExchangePacketFormatter.sellSuccessMessage());
        player.send(PlayerPacketFormatter.podsMessage(player.getStatistics().refreshPods()));
    }
}
