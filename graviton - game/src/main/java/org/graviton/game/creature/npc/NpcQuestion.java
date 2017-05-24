package org.graviton.game.creature.npc;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.xml.Attribute;
import org.graviton.xml.XMLElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Botan on 07/12/2016. 21:19
 */

@Data
public class NpcQuestion {
    private final short id;
    private final String parameter;
    private final String answersData;
    private final Map<Short, List<NpcAnswer>> answers = new HashMap<>();
    private final String conditions;
    private List<NpcAnswer> alternative;

    public NpcQuestion(XMLElement element, List<NpcAnswer> answers) {
        this.id = element.getAttribute("id").toShort();
        this.parameter = element.getElementByTagName("parameters").toString();

        answersData = element.getElementByTagName("answers").toString();

        if (!answersData.isEmpty())
            for (String answer : answersData.split(";")) {
                short answerId = Short.parseShort(answer);
                this.answers.put(answerId, answers.stream().filter(current -> current.getId() == answerId).collect(Collectors.toList()));
            }

        Attribute alternativeData = element.getElementByTagName("alternative");

        if (!alternativeData.toString().isEmpty())
            this.alternative = answers.stream().filter(current -> current.getId() == alternativeData.toShort()).collect(Collectors.toList());

        this.conditions = element.getElementByTagName("conditions").toString();
    }

    public String toString(Player player) {
        return id + convertParameter(player) + (answersData.isEmpty() ? "" : "|" + answersData);
    }

    private String convertParameter(Player player) {
        return parameter.isEmpty() ? "" : ";" + parameter.replace("[name]", player.getName()).replace("[bankCost]", String.valueOf(player.getAccount().getBank().getCost()));
    }


}
