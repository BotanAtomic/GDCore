package org.graviton.game.exchange.type;

import org.graviton.constant.Dofus;
import org.graviton.game.client.player.Player;
import org.graviton.game.exchange.Exchange;
import org.graviton.game.items.Item;
import org.graviton.game.job.action.JobAction;
import org.graviton.game.job.action.type.Craft;
import org.graviton.game.job.craft.CraftData;
import org.graviton.network.game.protocol.JobPacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Botan on 04/07/17. 17:06
 */
public class BreakerExchange implements Exchange {
    private final Player player;
    private final JobAction jobAction;
    private final ScheduledExecutorService scheduler;

    private boolean stopCraft, isRepeat;

    private short itemCraft;

    private Map<Short, Short> ingredients, lastIngredients;

    public BreakerExchange(Player player, JobAction jobAction, ScheduledExecutorService scheduler) {
        this.player = player;
        this.jobAction = jobAction;
        this.scheduler = scheduler;

        this.ingredients = new HashMap<>();
        this.lastIngredients = new HashMap<>();
    }

    @Override
    public void accept() {

    }

    @Override
    public void cancel() {
        this.jobAction.stop(player, null);
    }

    @Override
    public short getItemQuantity(int exchangerId, int itemId) {
        return 0;
    }

    @Override
    public void addItem(int itemId, short quantity, int exchangerId) {
        short template = player.getInventory().get(itemId).getTemplate().getId();
        this.ingredients.put(template, (short) (this.ingredients.getOrDefault(template, (short) 0) + quantity));
        player.send(JobPacketFormatter.addItemMessage(itemId, this.ingredients.get(template)));
    }

    @Override
    public void removeItem(int itemId, short quantity, int exchangerId) {
        short template = player.getInventory().get(itemId).getTemplate().getId();
        this.ingredients.put(template, (short) (this.ingredients.getOrDefault(template, (short) 0) - quantity));
        player.send(JobPacketFormatter.removeItemMessage(itemId, this.ingredients.get(template)));

    }

    @Override
    public void editKamas(long quantity, int exchangerId) {

    }

    @Override
    public void toggle(int exchangerId) {
        if (exchangerId < 0) {
            isRepeat = true;
            scheduler.schedule(() -> repeatCraft(((Craft) jobAction).get(ingredients), (exchangerId * -1) + 1), 1, TimeUnit.SECONDS);
        } else {
            this.lastIngredients.putAll(ingredients);
            scheduler.schedule(() -> craft(((Craft) jobAction).get(ingredients)), 1, TimeUnit.SECONDS);
        }
    }

    @Override
    public void buy(int itemId, short quantity) {

    }

    @Override
    public void sell(int itemId, short quantity) {

    }

    private void craft(CraftData craftData) {
        if (isRepeat)
            return;

        ingredients.forEach((itemId, quantity) -> {
            Item item = player.getInventory().getItemByTemplate(itemId);
            player.getInventory().setItemQuantity(item, (short) (item.getQuantity() - quantity));
        });

        player.send(PlayerPacketFormatter.podsMessage(player.getStatistics().refreshPods()));

        if (craftData != null) {
            Item result = player.getEntityFactory().getItemTemplate(craftData.getResult()).createMax(player.getEntityFactory().getNextItemId());
            player.getInventory().addItem(result, true);
            player.send(JobPacketFormatter.addCraftItemMessage(player.getInventory().getItemByTemplate(craftData.getResult()).getId(), craftData.getResult()));
            player.send(JobPacketFormatter.createCraftResultMessage(craftData.getResult()));
            player.getMap().send(JobPacketFormatter.successIOMessage(player.getId(), craftData.getResult()));

            jobAction.getJob().addExperience(player, Dofus.calculXpWinCraft(jobAction.getJob().getLevel(), (byte) craftData.getIngredients().size()));

        } else {
            player.send(JobPacketFormatter.emptyCraftMessage());
            player.getMap().send(JobPacketFormatter.badIOMessage(player.getId()));
        }
        this.ingredients.clear();
    }

    private void repeatCraft(CraftData craftData, int repetition) {
        ingredients.forEach((itemId, quantity) -> {
            Item item = player.getInventory().getItemByTemplate(itemId);
            player.getInventory().setItemQuantity(item, (short) (item.getQuantity() - quantity));
        });

        player.send(PlayerPacketFormatter.podsMessage(player.getStatistics().refreshPods()));

        if (craftData != null) {
            itemCraft++;

            if ((repetition - itemCraft == 0) || stopCraft) {
                finish(stopCraft);
                player.send(JobPacketFormatter.createCraftResultMessage(craftData.getResult()));
            } else {
                player.send(JobPacketFormatter.craftRepetitionMessage((short) (repetition - (itemCraft))));
                scheduler.schedule(() -> repeatCraft(craftData, repetition), 1, TimeUnit.SECONDS);
            }

            if (((Craft) jobAction).getChance() - new SecureRandom().nextInt(101) < 0) {
                player.send(JobPacketFormatter.errorChanceCraftMessage());
                player.send(MessageFormatter.customMessage("0118"));
                player.getMap().send(JobPacketFormatter.badIOMessage(player.getId()));
                return;
            }

            Item result = player.getEntityFactory().getItemTemplate(craftData.getResult()).createMax(player.getEntityFactory().getNextItemId());
            player.getInventory().addItem(result, true);
            player.send(JobPacketFormatter.addCraftItemMessage(player.getInventory().getItemByTemplate(craftData.getResult()).getId(), craftData.getResult()));
            player.getMap().send(JobPacketFormatter.successIOMessage(player.getId(), craftData.getResult()));
            jobAction.getJob().addExperience(player, Dofus.calculXpWinCraft(jobAction.getJob().getLevel(), (byte) craftData.getIngredients().size()));
        } else {
            player.send(JobPacketFormatter.interruptCraftMessage());
            player.getMap().send(JobPacketFormatter.badIOMessage(player.getId()));
        }
    }


    public void finish(boolean broken) {
        if (!broken) player.send(JobPacketFormatter.craftRepetitionMessage((short) 0));

        ingredients.forEach((itemId, quantity) -> {
            int playerItem = player.getInventory().getItemByTemplate(itemId).getId();
            addItem(playerItem, (short) 1, player.getId());
            removeItem(playerItem, (short) 1, player.getId());
        });

        player.send(JobPacketFormatter.craftResultMessage(broken));

        stopCraft = false;
        isRepeat = false;
        itemCraft = 0;
        ingredients.clear();
    }

    public void setLastIngredients() {
        this.lastIngredients.forEach((ingredient, quantity) -> {
            Item item = player.getInventory().getItemByTemplate(ingredient);
            if (item != null && (item.getQuantity() >= quantity))
                addItem(item.getId(), quantity, -1);
        });
        this.lastIngredients.clear();
    }

    public void stopCraft() {
        this.stopCraft = true;
    }

}
