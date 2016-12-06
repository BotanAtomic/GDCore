package org.graviton.game.creature.monster;

import lombok.Data;
import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;
import org.w3c.dom.Element;

import java.util.*;

/**
 * Created by Botan on 02/12/2016. 21:32
 */
@Data
public class MonsterTemplate {
    private final int id;
    private final String name;
    private final short skin;
    private final byte alignment;
    private final String colors;
    private final short[] winKamas;
    private final byte artificialIntelligence;
    private final boolean capture;
    private final byte aggressionDistance;

    private final Collection<Monster> monsters;

    private final Element element;

    public MonsterTemplate(Element element) {
        this.element = element;
        this.id = Integer.parseInt(element.getAttribute("id"));
        this.name = getTag("name");
        this.skin = Short.parseShort(getTag("skin"));
        this.alignment = Byte.parseByte(getTag("alignment"));
        this.colors = getTag("colors");
        this.winKamas = new short[]{Short.parseShort(getTagElement("kamas", "min")), Short.parseShort(getTagElement("kamas", "max"))};
        this.artificialIntelligence = Byte.parseByte(getTag("IA"));
        this.capture = Boolean.parseBoolean(getTag("capture"));
        this.aggressionDistance = Byte.parseByte(getTag("aggression"));
        this.monsters = this.loadMonsters();
    }

    private Collection<Monster> loadMonsters() {
        Collection<Monster> monsters = Collections.synchronizedCollection(new ArrayList<>());

        String[] grade = getTag("grades").split("\\|");
        String[] initiative = getTag("initiative").split("\\|");
        String[] experience = getTag("experience").split("\\|");
        String[] points = getTag("points").split("\\|");
        String[] life = getTag("life").split("\\|");
        String[] optionalStatistics = getTag("statisticsInfo").split(";");
        String[] statistics = getTag("statistics").split("\\|");

        Map<CharacteristicType, Characteristic> baseCharacteristics = new HashMap<CharacteristicType, Characteristic>() {{
            put(CharacteristicType.Damage, new Characteristic(Short.parseShort(optionalStatistics[0])));
            put(CharacteristicType.DamagePer, new Characteristic(Short.parseShort(optionalStatistics[1])));
            put(CharacteristicType.HealPoints, new Characteristic(Short.parseShort(optionalStatistics[2])));
            put(CharacteristicType.Summons, new Characteristic(Short.parseShort(optionalStatistics[3])));
        }};

        for (int i = 0; i < grade.length; i++) {
            Map<CharacteristicType, Characteristic> characteristics = new HashMap<>();
            characteristics.putAll(baseCharacteristics);

            baseCharacteristics.put(CharacteristicType.Initiative, new Characteristic(Short.parseShort(initiative[i])));
            baseCharacteristics.put(CharacteristicType.ActionPoints, new Characteristic(Short.parseShort(points[i].split(";")[0])));
            baseCharacteristics.put(CharacteristicType.MovementPoints, new Characteristic(Short.parseShort(points[i].split(";")[1])));

            monsters.add(new Monster(this, ((byte) (i + 1)), Short.parseShort(grade[i].split("@")[0]), Integer.parseInt(experience[i]), grade[i], life[i], statistics[i], characteristics));
        }

        return monsters;
    }

    private String getTag(String tag) {
        return this.element.getElementsByTagName(tag).item(0).getTextContent().trim();
    }

    private String getTagElement(String tag, String attribute) {
        return ((Element) this.element.getElementsByTagName(tag).item(0)).getAttribute(attribute);
    }

    public Monster getByLevel(short level) {
        return this.monsters.stream().filter(monster -> monster.getLevel() == level).findFirst().get();
    }

}
