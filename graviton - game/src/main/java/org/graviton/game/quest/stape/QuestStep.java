package org.graviton.game.quest.stape;

import javafx.util.Pair;
import lombok.Data;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.quest.Quest;
import org.graviton.game.quest.QuestGoal;
import org.graviton.xml.XMLElement;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * Created by Botan on 13/07/17. 02:32
 */

@Data
public class QuestStep {

    private final short id, npc, validation;
    private final List<Pair<Short, Short>> items = new CopyOnWriteArrayList<>();
    private final boolean condition;

    private Pair<Short, Byte> monster;

    private final QuestGoal questGoal;
    private final QuestStepValidation questStepValidation;

    public QuestStep(XMLElement element, EntityFactory entityFactory) {
        this.id = element.getAttribute("id").toShort();
        this.npc = element.getAttribute("npc").toShort();
        this.validation = element.getAttribute("validation").toShort();
        this.condition = "1".equals(element.getAttribute("condition").toString());

        this.questGoal = entityFactory.getQuestGoals().get(element.getAttribute("goal").toShort());
        this.questStepValidation = QuestStepValidationType.get(element.getAttribute("type").toByte()).create();

        String items = element.getAttribute("item").toString();
        if (!items.isEmpty())
            Stream.of(items.split(";")).forEach(itemData -> {
                String[] data = itemData.split(",");
                this.items.add(new Pair<>(Short.parseShort(data[0]), (data.length > 1 ? Short.parseShort(data[1]) : 1)));
            });

        String monster = element.getAttribute("monster").toString();
        if (!monster.isEmpty()) {
            String[] monsterData = monster.split(",");
            this.monster = new Pair<>(Short.parseShort(monsterData[0]), monsterData.length > 1 ? Byte.parseByte(monsterData[1]) : 1);
        }
    }

    public boolean update(Quest quest,QuestStepValidationType type, String... data) {
        return questStepValidation.type() == type && questStepValidation.validate(quest,this, data);
    }


}
