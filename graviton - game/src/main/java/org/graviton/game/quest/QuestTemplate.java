package org.graviton.game.quest;

import javafx.util.Pair;
import lombok.Data;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.client.player.Player;
import org.graviton.game.quest.stape.QuestStep;
import org.graviton.xml.XMLElement;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Created by Botan on 13/07/17. 01:28
 */

@Data
public class QuestTemplate {
    private final short id;
    private final Map<Byte, QuestStep> steps = Collections.synchronizedMap(new TreeMap<>());
    private final List<QuestGoal> goals = new CopyOnWriteArrayList<>();
    private final short npc;
    private final List<Pair<Short, String>> actions = new CopyOnWriteArrayList<>();
    private final boolean temporary;
    private Pair<Short, Short> condition;

    public QuestTemplate(XMLElement element, EntityFactory entityFactory) {
        this.id = element.getAttribute("id").toShort();
        this.npc = element.getAttribute("npc").toShort();
        this.temporary = "1".equals(element.getAttribute("temporary").toString());

        final AtomicInteger index = new AtomicInteger(0);

        String steps = element.getAttribute("steps").toString();
        if (!steps.isEmpty()) {
            Stream.of(steps.split(";")).forEach(stepId -> {
                this.steps.put((byte) index.getAndIncrement(), entityFactory.getQuestSteps().get(Short.parseShort(stepId)));
            });
        }
        String goals = element.getAttribute("goal").toString();
        if (!goals.isEmpty())
            Stream.of(goals.split(";")).forEach(goalId -> this.goals.add(entityFactory.getQuestGoals().get(Short.parseShort(goalId))));

        String condition = element.getAttribute("condition").toString();
        if (!condition.isEmpty())
            this.condition = new Pair<>(Short.parseShort(condition.split(":")[0]), Short.parseShort(condition.split(":")[1]));

        String actions = element.getAttribute("action").toString();
        if (!actions.isEmpty()) {
            String[] arguments = element.getAttribute("arguments").split(";");
            index.set(0);
            Stream.of(actions.split(",")).forEach(actionId -> {
                this.actions.add(new Pair<>(Short.parseShort(actionId), arguments.length >= index.get() ? "" : arguments[index.get()]));
                index.incrementAndGet();
            });
        }

    }

    public boolean respectCondition(Player player) {
        if (this.condition == null) return true;

        switch (condition.getKey()) {
            case 1:
                if (player.getLevel() < condition.getValue())
                    return false;
                break;

            default:
                return true;
        }
        return true;
    }
}
