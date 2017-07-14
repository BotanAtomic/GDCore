package org.graviton.game.quest;

import lombok.Data;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.client.player.Player;
import org.graviton.game.items.Item;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.game.quest.stape.QuestStep;
import org.graviton.game.quest.stape.QuestStepValidationType;
import org.graviton.network.game.protocol.ItemPacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;
import org.graviton.network.game.protocol.QuestPacketFormatter;
import org.jooq.Record;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.graviton.database.jooq.game.tables.PlayerQuest.PLAYER_QUEST;

/**
 * Created by Botan on 13/07/17. 01:25
 */

@Data
public class Quest {
    private final QuestTemplate quest;
    private final Map<Short, QuestStep> validateSteps = new TreeMap<>();

    private Player player;
    private boolean finish;

    private Map<Short, Short> monstersKilled = new HashMap<>();

    public Quest(Record record, EntityFactory entityFactory) {
        this.quest = entityFactory.getQuests().get(record.get(PLAYER_QUEST.QUEST_ID));
        this.finish = record.get(PLAYER_QUEST.FINISH) == 1;
        String steps = record.get(PLAYER_QUEST.STEPS);

        if (!steps.isEmpty())
            Stream.of(steps.split(";")).forEach(stepId -> validateSteps.put(Short.parseShort(stepId), entityFactory.getQuestSteps().get(Short.parseShort(stepId))));
    }

    public Quest(Player player, QuestTemplate questTemplate) {
        this.quest = questTemplate;
        this.player = player;
    }

    public short previousGoal() {
        if (finish) return 0;

        int index = validateSteps.size() - 1;

        System.err.println("index = " + index);
        System.err.println(quest.getSteps().keySet());

        if (quest.getSteps().get((byte) index) != null)
            return quest.getSteps().get((byte) index).getQuestGoal().getId();

        return 0;
    }

    public short currentGoal() {
        if (finish) return 0;

        int index = validateSteps.size();

        if (quest.getSteps().get((byte) index) != null)
            return quest.getSteps().get((byte) index).getQuestGoal().getId();

        return 0;
    }

    public QuestStep currentStep() {
        if (finish) return null;
        int index = validateSteps.size();

        if (quest.getSteps().get((byte) index) != null)
            return quest.getSteps().get((byte) index);

        return null;
    }

    public short nextGoal() {
        if (finish) return 0;

        int index = validateSteps.size() + 1;

        if (quest.getSteps().get((byte) index) != null)
            return quest.getSteps().get((byte) index).getQuestGoal().getId();

        return 0;
    }


    public boolean respectCondition(QuestStep step) {
        AtomicBoolean value = new AtomicBoolean(true);
        if (step.isCondition()) {
            this.quest.getSteps().forEach((stepId, currentStep) -> {
                if (currentStep.getId() != step.getId() && !validateSteps.containsKey(currentStep.getId()))
                    value.set(false);
            });
        }
        return value.get();
    }

    public void update(QuestStepValidationType type, short validation, Player player, String... data) {
        QuestStep currentStep = currentStep();

        if (currentStep != null && currentStep.getValidation() == validation && currentStep.getQuestStepValidation().type() == type && respectCondition(currentStep)) {
            if (currentStep.update(this, type, data)) {
                validateSteps.put(currentStep.getId(), currentStep);

                applyReward(player);

                if (validateSteps.size() == quest.getSteps().size()) {
                    player.send(QuestPacketFormatter.finishQuestStaticMessage(quest.getId()));
                    this.finish = true;
                } else
                    player.send(QuestPacketFormatter.updateQuestStaticMessage(quest.getId()));
            }
        }
    }

    private void applyReward(Player player) {
        QuestGoal questGoal = quest.getGoals().stream().filter(goal -> goal.getId() == previousGoal()).findAny().orElse(null);
        EntityFactory entityFactory = player.getAccount().getClient().getEntityFactory();

        if (questGoal != null) {
            if (questGoal.getKamas() > 0) {
                player.getInventory().addKamas(questGoal.getKamas());
                player.send(MessageFormatter.kamasWinMessage(questGoal.getKamas()));
            }

            if (questGoal.getExperience() > 0) {
                player.getStatistics().addExperience(questGoal.getExperience());
                player.send(MessageFormatter.experienceWinMessage(questGoal.getExperience()));
            }

            if (!questGoal.getItems().isEmpty()) {
                questGoal.getItems().forEach(itemData -> {
                    Item item = entityFactory.getItemTemplate(itemData.getKey()).createRandom(entityFactory.getNextItemId());
                    item.setQuantity(itemData.getValue());

                    if (player.getInventory().addItem(item, true) == null)
                        player.send(ItemPacketFormatter.addItemMessage(item));

                    player.send(MessageFormatter.itemWinMessage(item.getTemplate().getId(), itemData.getValue()));
                });
            }

            player.send(PlayerPacketFormatter.asMessage(player));

            questGoal.getActions().forEach(actionData -> entityFactory.getActionRepository().create(actionData.getKey()).apply(player.getAccount().getClient(), actionData.getValue()));
        }

    }

    public void incrementMonsterKilled(short monster) {
        this.monstersKilled.put(monster, (short) (this.monstersKilled.getOrDefault(monster, (short) 0) + 1));
    }


}
