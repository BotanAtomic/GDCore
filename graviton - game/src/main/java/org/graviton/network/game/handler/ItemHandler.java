package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.client.player.Player;
import org.graviton.game.items.Item;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.ItemPacketFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

/**
 * Created by Botan on 05/12/2016. 16:31
 */
@Slf4j
public class ItemHandler {
    private final GameClient client;

    public ItemHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, byte subHeader) { // 'O'
        switch (subHeader) {
            case 77: // 'M'
                objectMove(data.split("\\|"));
                break;

            case 85: // 'U'
                objectUse(client.getPlayer().getInventory().get(Integer.parseInt(data.split("\\|")[0])));
                break;

            default:
                log.error("not implemented item packet '{}'", (char) subHeader);
        }
    }

    private void objectUse(Item item) {
        item.getTemplate().applyAction(client);

        item.changeQuantity((short) -1);

        if (item.getQuantity() <= 0) {
            client.getPlayer().removeItem(item);
            client.send(ItemPacketFormatter.deleteMessage(item.getId()));
        } else
            client.send(ItemPacketFormatter.quantityMessage(item.getId(), item.getQuantity()));

    }

    private void addItemShortcut(Player player, Item item, Item sliced, ItemPosition position, short quantity) {
        sliced.setQuantity(quantity);
        sliced.setPosition(position);

        item.changeQuantity((short) -quantity);

        if (item.getQuantity() <= 0) {
            client.getPlayer().removeItem(item);
            client.send(ItemPacketFormatter.deleteMessage(item.getId()));
        } else
            client.send(ItemPacketFormatter.quantityMessage(item.getId(), item.getQuantity()));


        player.getInventory().addItem(sliced, true);
        client.send(ItemPacketFormatter.addItemMessage(sliced));
        client.send(ItemPacketFormatter.itemMovementMessage(sliced.getId(), position));
    }


    private void objectMove(String data[]) {
        Player player = client.getPlayer();

        Item item = player.getInventory().get(Integer.parseInt(data[0]));
        ItemPosition position = ItemPosition.get(Byte.parseByte(data[1]));
        ItemPosition lastPosition;

        if (position == null)
            return;

        if (data.length > 2) {
            addItemShortcut(player, item, item.clone(client.getEntityFactory().getNextItemId()), position, Short.parseShort(data[2]));
            return;
        }

        Item same;
        if (!position.equipped() && (same = player.getInventory().same(item, position)) != null) {
            same.changeQuantity((short) 1);
            item.setQuantity((short) 0);

            client.getPlayer().removeItem(item);
            client.send(ItemPacketFormatter.deleteMessage(item.getId()));
            client.send(ItemPacketFormatter.quantityMessage(same.getId(), same.getQuantity()));
        } else {
            if (item.getQuantity() > 1) {
                Item sliced = item.clone(client.getEntityFactory().getNextItemId());

                player.getInventory().addItem(sliced, false);

                client.send(ItemPacketFormatter.addItemMessage(sliced));
                client.send(ItemPacketFormatter.quantityMessage(item.getId(), item.getQuantity()));

                item = sliced;
            }

            client.send(ItemPacketFormatter.itemMovementMessage(item.getId(), position));
        }

        lastPosition = item.getPosition();
        item.setPosition(position);

        applyEffects(player, item);

        client.send(PlayerPacketFormatter.podsMessage(player.getPods()));

        if (item.getPosition().needUpdate() || lastPosition.needUpdate())
            player.getMap().send(GamePacketFormatter.updateAccessories(player.getId(), PlayerPacketFormatter.gmsMessage(player)));
    }

    private void applyEffects(Player player, Item item) {
        player.getStatistics().applyItemEffects(item);
        client.send(PlayerPacketFormatter.asMessage(player, client.getEntityFactory().getExperience(player.getLevel()), player.getAlignment(), player.getStatistics()));
        client.getPlayer().getStatistics().refreshPods();
    }


}
