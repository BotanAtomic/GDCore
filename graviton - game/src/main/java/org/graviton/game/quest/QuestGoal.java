package org.graviton.game.quest;

import javafx.util.Pair;
import lombok.Data;
import org.graviton.xml.XMLElement;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * Created by Botan on 13/07/17. 01:29
 */

@Data
public class QuestGoal {
    private final short id;
    private final long kamas, experience;

    private final List<Pair<Short,Short>> items = new CopyOnWriteArrayList<>();
    private final List<Pair<Short, String>> actions = new CopyOnWriteArrayList<>();

    public QuestGoal(XMLElement element) {
        this.id = element.getAttribute("id").toShort();
        this.kamas = element.getAttribute("kamas").toLong();
        this.experience = element.getAttribute("experience").toLong();

        String items = element.getAttribute("item").toString();
        if(!items.isEmpty())
            Stream.of(items.split(";")).forEach(itemData -> {
                String[] data = itemData.split(",");
                this.items.add(new Pair<>(Short.parseShort(data[0]), (data.length > 1 ? Short.parseShort(data[1]) : 1)));
            });

        String actions = element.getAttribute("action").toString();
        if(!actions.isEmpty()) {
            Stream.of(actions.split(";")).forEach(action -> {
                String[] actionData = action.split("\\|");
                this.actions.add(new Pair<>(Short.parseShort(actionData[0]), actionData.length > 1 ? actionData[1] : ""));
            });
        }
    }
}
