package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.client.player.Player;
import org.graviton.game.items.Item;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.game.job.Job;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.*;

/**
 * Created by Botan on 05/12/2016. 16:31
 */
@Slf4j
public class ItemHandler {
    private final GameClient client;

    public ItemHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, char subHeader) {
        switch (subHeader) {
            case 'd':
                delete(data.split("\\|"));
                break;

            case 'D':
                drop(data.split("\\|"));
                break;

            case 'M':
                objectMove(data.split("\\|"));
                break;

            case 'U':
                objectUse(client.getPlayer().getInventory().get(Integer.parseInt(data.split("\\|")[0])));
                break;

            default:
                log.error("not implemented item packet '{}'", subHeader);
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
        objectMove(client.getPlayer().getInventory().get(Integer.parseInt(data[0])), ItemPosition.get(Byte.parseByte(data[1])), data);
    }

    private synchronized void objectMove(Item item, ItemPosition position, String[] data) {
        Player player = client.getPlayer();

        if (position == null)
            return;

        if (data.length > 2) {
            addItemShortcut(player, item, item.clone(client.getEntityFactory().getNextItemId()), position, Short.parseShort(data[2]));
            return;
        }

        Item same;
        if (!position.equipped() && (same = player.getInventory().same(item, position)) != null) {
            duplicateItem(item, same);
        } else {
            if (position.equipped() && !checkConditions(player, item.getTemplate()))
                return;

            if (item.getQuantity() > 1)
                item = cloneItem(player, item);

            client.send(ItemPacketFormatter.itemMovementMessage(item.getId(), position));
        }

        ItemPosition lastPosition = item.getPosition();
        item.setPosition(position);

        applyEffects(player, item);
        client.send(PlayerPacketFormatter.podsMessage(player.getPods()));
        // checkItemsCondition(player);

        if (item.getPosition().equipped() && !player.getJobs().isEmpty()) {
            Item secureCopy = item;

            Job selectedJob = player.getJobs().values().stream().filter(job -> job.getJobTemplate().getTools().contains(secureCopy.getTemplate().getId())).findAny().orElse(null);

            if (selectedJob != null)
                player.send(JobPacketFormatter.jobToolMessage(selectedJob.getJobTemplate().getId()));
        } else if (!item.getPosition().equipped() && !player.getJobs().isEmpty())
            player.send(JobPacketFormatter.jobToolMessage((short) 0));


        if (item.getPosition().needUpdate() || lastPosition.needUpdate())
            player.getMap().send(GamePacketFormatter.updateAccessories(player.getId(), PlayerPacketFormatter.gmsMessage(player)));
    }

    private Item cloneItem(Player player, Item item) {
        Item sliced = item.clone(client.getEntityFactory().getNextItemId());

        player.getInventory().addItem(sliced, false);

        client.send(ItemPacketFormatter.addItemMessage(sliced));
        client.send(ItemPacketFormatter.quantityMessage(item.getId(), item.getQuantity()));
        return sliced;
    }

    private void duplicateItem(Item item, Item same) {
        same.changeQuantity((short) 1);
        item.setQuantity((short) 0);

        client.getPlayer().removeItem(item);
        client.send(ItemPacketFormatter.deleteMessage(item.getId()));
        client.send(ItemPacketFormatter.quantityMessage(same.getId(), same.getQuantity()));
    }

    private void applyEffects(Player player, Item item) {
        player.getStatistics().applyItemEffects(item);
        client.getPlayer().getStatistics().refreshPods();

        if (item.getTemplate().getPanoply() != null)
            player.getStatistics().applyPanoplyEffect(item.getTemplate().getPanoply(), item.getPosition().equipped(), true);

        client.send(PlayerPacketFormatter.asMessage(player, client.getEntityFactory().getExperience(player.getLevel()), player.getAlignment(), player.getStatistics()));
    }

    private void checkItemsCondition(Player player) {
        player.getInventory().getEquippedItems().forEach(item -> {
            if (!item.getTemplate().getConditionList().check(player))
                objectMove(item, ItemPosition.NotEquipped, new String[]{});
        });
    }

    private boolean checkConditions(Player player, ItemTemplate template) {
        if (player.getLevel() < template.getLevel()) {
            player.send(MessageFormatter.levelRequiredErrorMessage());
            return true; //TODO : set false
        } else if (!template.getConditionList().check(player)) {
            player.send(MessageFormatter.conditionErrorMessage());
            return true; //TODO : set false
        }
        return true;
    }

    private void delete(String[] data) {
        Item item = client.getPlayer().getInventory().get(Integer.parseInt(data[0]));
        short newQuantity = (short) (item.getQuantity() - Short.parseShort(data[1]));

        if (newQuantity <= 0) {
            client.getPlayer().removeItem(item);
            client.send(ItemPacketFormatter.deleteMessage(item.getId()));
        } else {
            item.setQuantity(newQuantity);
            client.send(ItemPacketFormatter.quantityMessage(item.getId(), newQuantity));
        }
    }

    private void drop(String[] data) {
        Item item = client.getPlayer().getInventory().get(Integer.parseInt(data[0]));
        short newQuantity = (short) (item.getQuantity() - Short.parseShort(data[1]));

        if (newQuantity <= 0) {

        } else {

        }

    }


}
