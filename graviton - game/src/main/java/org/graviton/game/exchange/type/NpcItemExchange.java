package org.graviton.game.exchange.type;

import org.graviton.database.entity.EntityFactory;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.npc.Npc;
import org.graviton.game.creature.npc.exchange.ItemExchangeTemplate;
import org.graviton.game.exchange.Exchange;
import org.graviton.game.exchange.Exchanger;
import org.graviton.game.items.Item;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.network.game.protocol.ExchangePacketFormatter;
import org.graviton.network.game.protocol.ItemPacketFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Botan on 24/05/17. 14:40
 */
public class NpcItemExchange implements Exchange {
    private final Exchanger exchanger;
    private final Npc npc;

    private final Map<Short, Short> npcItems = new HashMap<>();

    private boolean validate;

    public NpcItemExchange(Player player, Npc npc) {
        this.exchanger = new Exchanger(player, this);
        this.npc = npc;
    }

    @Override
    public void accept() {
        exchanger.send(ExchangePacketFormatter.applyMessage());
        EntityFactory entityFactory = exchanger.getPlayer().getEntityFactory();

        npcItems.forEach((itemId, quantity) -> {
            Item item = entityFactory.getItemTemplate(itemId).createRandom(entityFactory.getNextItemId()), same = exchanger.getPlayer().getInventory().same(item);
            item.setQuantity(quantity);

            if(same == null) {
                exchanger.getPlayer().getInventory().addItem(item, true);
                exchanger.send(ItemPacketFormatter.addItemMessage(item));
            } else {
                same.setQuantity((short) (same.getQuantity() + quantity));
                exchanger.send(ItemPacketFormatter.quantityMessage(same.getId(), same.getQuantity()));
            }

        });

        exchanger.getItems().forEach((itemId, quantity) -> {
            Item item = exchanger.getPlayer().getInventory().get(itemId);

            if(item.getQuantity() - quantity <= 0) {
                exchanger.getPlayer().removeItem(item);
                exchanger.send(ItemPacketFormatter.deleteMessage(itemId));
            } else {
                item.setQuantity((short) (item.getQuantity() - quantity));
                exchanger.send(ItemPacketFormatter.quantityMessage(itemId, item.getQuantity()));
            }

        });

        exchanger.getPlayer().update();
        exchanger.getPlayer().getAccount().getClient().getInteractionManager().clear();
    }

    @Override
    public void cancel() {
        exchanger.send(ExchangePacketFormatter.cancelMessage());
    }

    @Override
    public short getItemQuantity(int exchangerId, int itemId) {
        return exchanger.getItems().getOrDefault(itemId, (short) 0);
    }

    @Override
    public void addItem(int itemId, short quantity, int exchangerId) {
        Item item = exchanger.getPlayer().getInventory().get(itemId);

        exchanger.getItems().put(itemId, (short) (quantity + getItemQuantity(0, itemId)));
        exchanger.send(ExchangePacketFormatter.addItemMessage(itemId, getItemQuantity(exchangerId, itemId), item.getTemplate().getId(), item.parse()));

        editNpcItems(npc.getTemplate().getExchangeParser().check(exchanger.getPlayer(), exchanger.getItems()));
    }

    @Override
    public void removeItem(int itemId, short quantity, int exchangerId) {
        Item item = exchanger.getPlayer().getInventory().get(itemId);

        short newQuantity = (short) (getItemQuantity(0, itemId) - quantity);
        if (newQuantity <= 0) {
            exchanger.getItems().remove(itemId);
            exchanger.send(ExchangePacketFormatter.removeItemMessage(itemId));
        } else {
            exchanger.getItems().put(itemId, newQuantity);
            exchanger.send(ExchangePacketFormatter.addItemMessage(itemId, getItemQuantity(exchangerId, itemId), item.getTemplate().getId(), item.parse()));
        }

        editNpcItems(npc.getTemplate().getExchangeParser().check(exchanger.getPlayer(), exchanger.getItems()));
    }

    @Override
    public void editKamas(long quantity, int exchangerId) {

    }

    @Override
    public void toggle(int exchangerId) {
        if(exchanger.toggle() && validate)
            accept();
        else
            exchanger.send(ExchangePacketFormatter.notificationMessage(exchanger.isReady(), exchangerId));
    }

    @Override
    public void buy(int itemId, short quantity) {

    }

    @Override
    public void sell(int itemId, short quantity) {

    }

    private void editNpcItems(List<ItemExchangeTemplate> items) {
        if (items.isEmpty()) {
            this.npcItems.keySet().forEach(item -> exchanger.send(ExchangePacketFormatter.removeOtherItemMessage(item)));
            exchanger.send(ExchangePacketFormatter.notificationMessage((validate = false), npc.getId()));
            npcItems.clear();
        } else {
            items.forEach(itemExchangeTemplate -> {
                ItemTemplate template = exchanger.getPlayer().getEntityFactory().getItemTemplate(itemExchangeTemplate.getItemId());
                npcItems.put(template.getId(), itemExchangeTemplate.getQuantity());
                exchanger.send(ExchangePacketFormatter.addOtherItemMessage(template.getId(), itemExchangeTemplate.getQuantity(), template.getId(), template.parseStatistics()));
            });
            exchanger.send(ExchangePacketFormatter.notificationMessage((validate = true), npc.getId()));
        }
    }

}
