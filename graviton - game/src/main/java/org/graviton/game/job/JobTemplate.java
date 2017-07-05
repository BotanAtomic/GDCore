package org.graviton.game.job;


import lombok.Data;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.job.action.JobActionGetter;
import org.graviton.game.job.craft.CraftData;
import org.graviton.utils.Utils;
import org.graviton.xml.XMLElement;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Botan on 02/04/2017. 22:23
 */

@Data
public class JobTemplate {
    private final short id;
    private final String name;
    private final List<Short> tools;
    private final boolean basic;

    private final Map<Short, List<CraftData>> crafts = new ConcurrentHashMap<>();
    private final Map<Short, List<Short>> skills = new ConcurrentHashMap<>();

    private JobActionGetter actionGetter;

    public JobTemplate(XMLElement element, EntityFactory entityFactory) {
        this.id = element.getAttribute("id").toShort();
        this.name = element.getAttribute("name").toString();
        this.tools = Utils.arrayToShortList(element.getSecureElementByTag("tools").toString(), ",");
        this.basic = Boolean.parseBoolean(element.getAttribute("basic").toString());


        String crafts = element.getElementByTagName("crafts").toString();
        if (!crafts.isEmpty())
            Stream.of(crafts.split("\\|")).forEach(data -> {
                short skill = Short.parseShort(data.split(";")[0]);
                List<CraftData> craftsData = new CopyOnWriteArrayList<>();
                Stream.of(data.split(";")[1].split(",")).forEach(craft -> craftsData.add(entityFactory.getCrafts().get(Short.parseShort(craft))));
                this.crafts.put(skill, craftsData);
            });

        String skills = element.getElementByTagName("skills").toString();
        if (!skills.isEmpty())
            Stream.of(skills.split("\\|")).forEach(skillsData -> {
                String extra = skillsData.split(";")[0], skill = skillsData.split(";")[1];
                List<Short> data = Stream.of(skill.split(",")).map(Short::parseShort).collect(Collectors.toCollection(CopyOnWriteArrayList::new));
                Stream.of(extra.split(",")).forEach(extraData -> this.skills.put(Short.parseShort(extraData), data));
            });

    }

    public List<CraftData> getCrafts(short skill, byte maxCase) {
        return crafts.get(skill).stream().filter(craft -> craft.getIngredients().size() <= maxCase).collect(Collectors.toList());
    }

}
