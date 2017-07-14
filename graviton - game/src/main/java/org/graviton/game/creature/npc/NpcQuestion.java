package org.graviton.game.creature.npc;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.game.filter.ConditionList;
import org.graviton.game.quest.stape.QuestStepValidationType;
import org.graviton.xml.Attribute;
import org.graviton.xml.XMLElement;
import org.graviton.game.action.npc.Quest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Botan on 07/12/2016. 21:19
 */

@Data
public class NpcQuestion {
    private final short id;
    private final String parameter;
    private final List<NpcAnswer> answers = new ArrayList<>();
    private final ConditionList conditionList;
    private short alternative;

    public NpcQuestion(XMLElement element, List<NpcAnswer> answers) {
        this.id = element.getAttribute("id").toShort();
        this.parameter = element.getElementByTagName("parameters").toString();

        String answersData = element.getElementByTagName("answers").toString();

        if (!answersData.isEmpty())
            for (String answer : answersData.split(";"))
                this.answers.add(answers.stream().filter(current -> current.getId() == Short.parseShort(answer)).findAny().orElse(null));

        this.alternative = element.getElementByTagName("alternative").toShort();

        this.conditionList = new ConditionList(element.getElementByTagName("conditions").toString());
    }

    public String toString(Player player) {
        if (!conditionList.getConditions().isEmpty() && conditionList.check(player))
            return player.getEntityFactory().getNpcQuestion(alternative).toString(player);
        else {
            List<NpcAnswer> answers = getAnswers(player);
            StringBuilder builder = new StringBuilder(String.valueOf(id)).append(convertParameter(player));
            if (!answers.isEmpty()) {
                builder.append("|");
                answers.forEach(answer -> {
                    builder.append(answer.getId()).append(";");
                    player.activeQuests().stream().filter(quest -> quest.currentStep().getValidation() == answer.getId()).forEach(quest -> quest.update(QuestStepValidationType.ITEM, answer.getId(), player));
                });
                return builder.substring(0, builder.length() - 1);
            }
            return builder.toString();
        }
    }

    private List<NpcAnswer> getAnswers(Player player) {
        return this.answers.stream().filter(npcAnswer -> {
            if (npcAnswer.getNpcAction() != null && npcAnswer.getNpcAction() instanceof Quest)
                if (player.getQuest(Short.parseShort(npcAnswer.getData())) != null)
                    return false;
            return true;
        }).collect(Collectors.toList());
    }

    private String convertParameter(Player player) {
        return parameter.isEmpty() ? "" : ";" + parameter.replace("[name]", player.getName()).replace("[bankCost]", String.valueOf(player.getAccount().getBank().getCost()));
    }


}
