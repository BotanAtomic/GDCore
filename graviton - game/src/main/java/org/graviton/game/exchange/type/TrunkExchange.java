package org.graviton.game.exchange.type;

import org.graviton.constant.Dofus;
import org.graviton.game.client.player.Player;
import org.graviton.game.exchange.Exchange;
import org.graviton.game.items.Item;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.game.trunk.AbstractTrunk;
import org.graviton.game.trunk.type.Bank;
import org.graviton.game.trunk.type.Trunk;
import org.graviton.network.game.protocol.ExchangePacketFormatter;
import org.graviton.network.game.protocol.ItemPacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

import java.util.stream.Stream;

import static org.graviton.lang.LanguageSentence.MAX_IN_TRUNK;

/**
 * Created by Botan on 13/05/17. 19:16
 */
public class TrunkExchange implements Exchange {
    private final Player player;
    private final AbstractTrunk trunk;

    public TrunkExchange(Player player, AbstractTrunk trunk) {
        this.player = player;
        this.trunk = trunk;
    }

    private void removeItem(Item item, boolean partial) {
        player.send(ItemPacketFormatter.deleteMessage(item.getId()));
        if (partial)
            player.getInventory().remove(item.getId());
        else
            player.removeItem(item);
    }

    private void changeItemQuantity(Item item, short newQuantity) {
        player.send(ItemPacketFormatter.quantityMessage(item.getId(), newQuantity));
        item.setQuantity(newQuantity);
    }

    private void saveItems(Item... items) {
        Stream.of(items).forEach(item -> player.getEntityFactory().getPlayerRepository().saveItem(item, player.getId()));
    }

    @Override
    public void accept() {

    }

    @Override
    public void cancel() {
        player.send(ExchangePacketFormatter.cancelMessage());
        if (trunk instanceof Bank)
            player.getEntityFactory().getAccountRepository().updateBank(player.getAccount().getBank());
        else {
            player.getEntityFactory().getGameMapRepository().updateTrunk((Trunk) trunk);
            ((Trunk) trunk).setInUse(false);
        }
        player.update();
        player.getAccount().getClient().getInteractionManager().clear();
    }

    @Override
    public short getItemQuantity(int exchangerId, int itemId) {
        return 0;
    }

    @Override
    public void addItem(int itemId, short quantity, int exchangerId) {
        if(trunk.size() >= Dofus.MAX_OBJECT_IN_TRUNK) {
            player.send(MessageFormatter.customStaticMessage(player.getAccount().getClient().getLanguage().getSentence(MAX_IN_TRUNK)));
            return;
        }

        Item item = player.getInventory().get(itemId), same = trunk.same(item);

        short newQuantity = (short) (item.getQuantity() - quantity);

        if (same != null) {
            if (newQuantity <= 0) {
                removeItem(item, false);
                same.setQuantity((short) (same.getQuantity() + item.getQuantity()));
            } else {
                changeItemQuantity(item, newQuantity);
                same.setQuantity((short) (same.getQuantity() + quantity));
            }
            saveItems(item, same);
            player.send(ExchangePacketFormatter.addTrunkItemMessage(same.getId(), same.getQuantity(), same.getTemplate().getId(), same.parseEffects()));
        } else {
            if (newQuantity <= 0) {
                item.setPosition(ItemPosition.Bank);
                removeItem(item, true);
                trunk.addItem(item);
                player.send(ExchangePacketFormatter.addTrunkItemMessage(item.getId(), item.getQuantity(), item.getTemplate().getId(), item.parseEffects()));
                saveItems(item);
            } else {
                changeItemQuantity(item, newQuantity);
                Item clone = item.clone(player.getEntityFactory().getNextItemId());
                player.send(ExchangePacketFormatter.addTrunkItemMessage(clone.getId(), quantity, item.getTemplate().getId(), item.parseEffects()));
                clone.setQuantity(quantity);
                trunk.addItem(clone);
                clone.setPosition(ItemPosition.Bank);
                saveItems(item, clone);
                player.getEntityFactory().getPlayerRepository().createItem(clone, player.getId());
            }
        }

        player.send(PlayerPacketFormatter.podsMessage(player.getStatistics().refreshPods()));
    }

    @Override
    public void removeItem(int itemId, short quantity, int exchangerId) {
        Item item = trunk.getItem(itemId), same = player.getInventory().same(item);

        short newQuantity = (short) (item.getQuantity() - quantity);

        if (same != null) {
            same.setQuantity((short) (same.getQuantity() + quantity));
            player.send(ItemPacketFormatter.quantityMessage(same.getId(), same.getQuantity()));

            if (newQuantity <= 0) {
                player.send(ExchangePacketFormatter.simpleRemoveItemMessage(item.getId()));
                trunk.removeItem(item);
                player.getEntityFactory().getPlayerRepository().removeItem(item);
            } else {
                item.setQuantity(newQuantity);
                player.send(ExchangePacketFormatter.addTrunkItemMessage(item.getId(), item.getQuantity(), item.getTemplate().getId(), item.parseEffects()));
            }
            saveItems(item, same);
        } else {
            if (newQuantity <= 0) {
                player.send(ExchangePacketFormatter.simpleRemoveItemMessage(item.getId()));
                trunk.removeItem(item);
                player.getInventory().addItem(item, false);
                player.send(ItemPacketFormatter.addItemMessage(item));
                item.setPosition(ItemPosition.NotEquipped);
                saveItems(item);
            } else {
                Item clone = item.clone(player.getEntityFactory().getNextItemId());
                clone.setQuantity(quantity);
                item.setQuantity(newQuantity);
                player.getInventory().addItem(clone, true);
                clone.setPosition(ItemPosition.NotEquipped);
                player.send(ExchangePacketFormatter.addTrunkItemMessage(item.getId(), item.getQuantity(), item.getTemplate().getId(), item.parseEffects()));
                player.send(ItemPacketFormatter.addItemMessage(clone));
                saveItems(item, clone);
            }
        }

    }

    @Override
    public void editKamas(long quantity, int exchangerId) {
        player.getInventory().addKamas(-quantity);
        player.send(PlayerPacketFormatter.asMessage(player));
        player.send(ExchangePacketFormatter.trunkKamasEditMessage(trunk.getKamas() + quantity));
        trunk.setKamas(trunk.getKamas() + quantity);
    }

    @Override
    public void toggle(int exchangerId) {

    }

    @Override
    public void buy(int itemId, short quantity) {

    }

    @Override
    public void sell(int itemId, short quantity) {

    }
}
