package org.graviton.game.quest.stape.type;

import org.graviton.game.client.player.Player;
import org.graviton.game.items.Item;
import org.graviton.game.quest.Quest;
import org.graviton.game.quest.stape.QuestStep;
import org.graviton.game.quest.stape.QuestStepValidation;
import org.graviton.game.quest.stape.QuestStepValidationType;
import org.graviton.network.game.protocol.ItemPacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Botan on 13/07/17. 02:31
 */
public class ItemStep implements QuestStepValidation {

    @Override
    public QuestStepValidationType type() {
        return QuestStepValidationType.ITEM;
    }

    @Override
    public boolean validate(Quest quest, QuestStep questStep, String... arguments) {
        Player player = quest.getPlayer();

        if(!questStep.getItems().isEmpty()) {
            AtomicInteger counter = new AtomicInteger(0);
            questStep.getItems().forEach(itemData -> {
                Item item = player.getInventory().getItemByTemplate(itemData.getKey());
                if(item != null && item.getQuantity() >= itemData.getValue())
                    counter.incrementAndGet();
            });

            if(counter.get() == questStep.getItems().size()) {
                questStep.getItems().forEach(itemData -> {
                    Item item = player.getInventory().getItemByTemplate(itemData.getKey());

                    if(item.getQuantity() - itemData.getValue() > 0) {
                        item.setQuantity((short) (item.getQuantity() - itemData.getValue()));
                        player.send(ItemPacketFormatter.quantityMessage(item.getId(), item.getQuantity()));
                    } else {
                        player.removeItem(item);
                        player.send(ItemPacketFormatter.deleteMessage(item.getId()));
                    }
                    player.send(MessageFormatter.looseItemMessage(itemData.getValue(), itemData.getKey()));

                });
                return true;
            }

        }

        return false;
    }
}
