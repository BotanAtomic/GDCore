package org.graviton.game.exchange.type;

import org.graviton.game.client.player.Player;
import org.graviton.game.exchange.Exchange;
import org.graviton.game.exchange.Exchanger;
import org.graviton.game.interaction.Status;
import org.graviton.game.items.Item;
import org.graviton.network.game.protocol.ExchangePacketFormatter;
import org.graviton.network.game.protocol.ItemPacketFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;
import org.graviton.utils.Performer;

import java.util.Arrays;

/**
 * Created by Botan on 03/03/2017. 23:30
 */
public class PlayerExchange implements Exchange {
    private final Exchanger first, second;

    public PlayerExchange(Player first, Player second) {
        this.first = new Exchanger(first, this);
        this.second = new Exchanger(second, this);
    }

    @Override
    public void accept() {
        perform(player -> player.setStatus(Status.EXCHANGE));
        send(ExchangePacketFormatter.startMessage((byte) 1));
    }

    @Override
    public void cancel() {
        perform(player -> player.setStatus(Status.DEFAULT));
        send(ExchangePacketFormatter.cancelMessage());
    }

    @Override
    public short getItemQuantity(int exchangerId, int itemId) {
        return getExchanger(exchangerId).getItems().getOrDefault(itemId, (short) 0);
    }

    @Override
    public void addItem(int itemId, short quantity, int exchangerId) {
        cancelReady();

        Exchanger exchanger = getExchanger(exchangerId);
        Item item = exchanger.getPlayer().getInventory().get(itemId);

        exchanger.getItems().put(itemId, (short) (quantity + getItemQuantity(exchangerId, itemId)));

        exchanger.send(ExchangePacketFormatter.addItemMessage(itemId, getItemQuantity(exchangerId, itemId), item.getTemplate().getId(), item.parse()));
        getOtherExchanger(exchangerId).send(ExchangePacketFormatter.addOtherItemMessage(itemId, getItemQuantity(exchangerId, itemId), item.getTemplate().getId(), item.parse()));
    }

    @Override
    public void removeItem(int itemId, short quantity, int exchangerId) {
        cancelReady();

        Exchanger exchanger = getExchanger(exchangerId);
        Item item = exchanger.getPlayer().getInventory().get(itemId);

        short newQuantity = (short) (getItemQuantity(exchangerId, itemId) - quantity);
        if (newQuantity <= 0) {
            exchanger.getItems().remove(itemId);

            exchanger.send(ExchangePacketFormatter.removeItemMessage(itemId));
            getOtherExchanger(exchangerId).send(ExchangePacketFormatter.removeOtherItemMessage(itemId));
        } else {
            exchanger.getItems().put(itemId, newQuantity);

            exchanger.send(ExchangePacketFormatter.addItemMessage(itemId, getItemQuantity(exchangerId, itemId), item.getTemplate().getId(), item.parse()));
            getOtherExchanger(exchangerId).send(ExchangePacketFormatter.addOtherItemMessage(itemId, getItemQuantity(exchangerId, itemId), item.getTemplate().getId(), item.parse()));
        }
    }

    @Override
    public void editKamas(int quantity, int exchangerId) {
        cancelReady();
        Exchanger exchanger = getExchanger(exchangerId);
        exchanger.setKamas(quantity);

        exchanger.send(ExchangePacketFormatter.editKamasMessage(quantity));
        getOtherExchanger(exchangerId).send(ExchangePacketFormatter.editOtherKamasMessage(quantity));
    }

    @Override
    public void toggle(int exchangerId) {
        boolean ready = getExchanger(exchangerId).toggle();
        send(ExchangePacketFormatter.notificationMessage(ready, exchangerId));

        if (first.isReady() && second.isReady())
            apply();
    }

    @Override
    public void buy(int itemId, short quantity) {

    }

    @Override
    public void sell(int itemId, short quantity) {

    }

    private void send(String data) {
        Arrays.asList(first, second).forEach(exchanger -> exchanger.send(data));
    }

    private void perform(Performer<Player> performer) {
        Arrays.asList(first.getPlayer(), second.getPlayer()).forEach(performer::perform);
    }

    private void performToExchanger(Performer<Exchanger> performer) {
        Arrays.asList(first, second).forEach(performer::perform);
    }

    private Exchanger getExchanger(int id) {
        if (first.getPlayer().getId() == id)
            return first;
        return second;
    }

    private Exchanger getOtherExchanger(int id) {
        if (first.getPlayer().getId() == id)
            return second;
        return first;
    }

    private void cancelReady() {
        performToExchanger(exchanger -> {
            exchanger.setReady(false);
            send(ExchangePacketFormatter.notificationMessage(exchanger.isReady(), exchanger.getPlayer().getId()));
        });
    }

    private void apply() {
        perform(player -> {
            player.getInventory().addKamas(getOtherExchanger(player.getId()).getKamas());
            player.getInventory().addKamas(getExchanger(player.getId()).getKamas() * -1);
            setItem(player);
            player.send(ExchangePacketFormatter.applyMessage());
            player.setExchange(null);
            player.send(PlayerPacketFormatter.asMessage(player));
            player.update();
            player.setStatus(Status.DEFAULT);
        });
    }

    private void setItem(Player player) {
        Exchanger exchanger = getExchanger(player.getId());
        Exchanger other = getOtherExchanger(player.getId());

        other.getItems().forEach((itemId, quantity) -> {
            Item baseItem = other.getPlayer().getInventory().get(itemId);
            baseItem.changeQuantity((short) -quantity);

            Item item = baseItem.clone(player.getEntityFactory().getNextItemId());
            item.setQuantity(quantity);

            if (baseItem.getQuantity() <= 0) {
                other.getPlayer().removeItem(baseItem);
                other.send(ItemPacketFormatter.deleteMessage(baseItem.getId()));
            } else {
                other.send(ItemPacketFormatter.quantityMessage(baseItem.getId(), baseItem.getQuantity()));
                other.getPlayer().getEntityFactory().getPlayerRepository().saveItem(baseItem);
            }

            Item same = exchanger.getPlayer().getInventory().addItem(item, true);
            if (same == null)
                exchanger.send(ItemPacketFormatter.addItemMessage(item));
            else {
                exchanger.send(ItemPacketFormatter.quantityMessage(same.getId(), same.getQuantity()));
                exchanger.getPlayer().getEntityFactory().getPlayerRepository().saveItem(same);
            }
        });
    }
}
