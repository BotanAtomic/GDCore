package org.graviton.game.creature.npc;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.xml.Attribute;
import org.graviton.xml.XMLElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Botan on 07/12/2016. 21:19
 */

@Data
public class NpcQuestion {
    private final short id;
    private final String parameter;
    private final String answersData;
    private final Map<Short, NpcAnswer> answers = new HashMap<>();
    private final String conditions;
    private NpcAnswer alternative;

    public NpcQuestion(XMLElement element, Map<Short, NpcAnswer> answers) {
        this.id = element.getAttribute("id").toShort();
        this.parameter = element.getElementByTagName("parameters").toString();

        answersData = element.getElementByTagName("answers").toString();

        if (!answersData.isEmpty())
            for (String answer : answersData.split(";")) {
                short answerId = Short.parseShort(answer);
                this.answers.put(answerId, answers.get(answerId));
            }

        Attribute alternativeData = element.getElementByTagName("alternative");

        if (!alternativeData.toString().isEmpty())
            this.alternative = answers.get(alternativeData.toShort());

        this.conditions = element.getElementByTagName("conditions").toString();
    }

    public String toString(Player player) {
        return id + convertParameter(player) + "|" + answersData;
    }

    private String convertParameter(Player player) {
        return parameter.isEmpty() ? "" : ";" + parameter.replace("[name]", player.getName()).replace("[bankCost]", "0"); //TODO : bank cost
    }


}
