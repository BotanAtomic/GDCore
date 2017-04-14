package org.graviton.game.creature.monster;

import lombok.Data;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.alignment.Alignment;
import org.graviton.game.drop.Drop;
import org.graviton.game.spell.Spell;
import org.graviton.game.statistics.BaseCharacteristic;
import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.xml.XMLElement;

import java.util.*;
import java.util.stream.IntStream;

import static java.lang.Short.parseShort;

/**
 * Created by Botan on 02/12/2016. 21:32
 */
@Data
public class MonsterTemplate {
    private final EntityFactory entityFactory;

    private final int id;
    private final String name;
    private final short skin;
    private final Alignment alignment;
    private final String colors;
    private final short[] winKamas;
    private final byte artificialIntelligence;
    private final boolean capture;
    private final byte aggressionDistance;

    private final Map<Short, Monster> monsters;

    private final List<Drop> drops = new ArrayList<>();

    public MonsterTemplate(XMLElement element, EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
        this.id = element.getAttribute("id").toInt();
        this.name = element.getElementByTagName("name").toString();
        this.skin = element.getElementByTagName("skin").toShort();
        this.alignment = new Alignment(element.getElementByTagName("alignment").toByte(), 0, 0, false, null);
        this.colors = element.getElementByTagName("colors").toString();
        this.winKamas = new short[]{element.getElementByTagName("kamas", "min").toShort(), element.getElementByTagName("kamas", "max").toShort()};
        this.artificialIntelligence = element.getElementByTagName("IA").toByte();
        this.capture = element.getElementByTagName("capture").toBoolean();
        this.aggressionDistance = element.getElementByTagName("aggression").toByte();

        this.monsters = this.loadMonsters(element, entityFactory);
    }

    private Map<Short, Monster> loadMonsters(XMLElement element, EntityFactory entityFactory) {
        Map<Short, Monster> monsters = Collections.synchronizedMap(new HashMap<>());

        String[] grade = element.getElementByTagName("grades").toString().split("\\|");
        String[] initiative = element.getElementByTagName("initiative").toString().split("\\|");
        String[] experience = element.getElementByTagName("experience").toString().split("\\|");
        String[] points = element.getElementByTagName("points").toString().split("\\|");
        String[] life = element.getElementByTagName("life").toString().split("\\|");
        String[] optionalStatistics = element.getElementByTagName("statisticsInfo").toString().split(";");
        String[] statistics = element.getElementByTagName("statistics").toString().split("\\|");
        String[] spells = element.getElementByTagName("spells").toString().split("\\|");

        Map<CharacteristicType, Characteristic> baseCharacteristics = new HashMap<>();
        baseCharacteristics.put(CharacteristicType.Damage, new BaseCharacteristic(parseShort(optionalStatistics[0])));
        baseCharacteristics.put(CharacteristicType.DamagePer, new BaseCharacteristic(parseShort(optionalStatistics[1])));
        baseCharacteristics.put(CharacteristicType.HealPoints, new BaseCharacteristic(parseShort(optionalStatistics[2])));
        baseCharacteristics.put(CharacteristicType.Summons, new BaseCharacteristic(parseShort(optionalStatistics[3])));


        IntStream.range(0, grade.length).forEach(i -> {
            Map<CharacteristicType, Characteristic> characteristics = new HashMap<>();
            characteristics.putAll(baseCharacteristics);

            characteristics.put(CharacteristicType.Initiative, new BaseCharacteristic(parseShort(initiative[i])));
            characteristics.put(CharacteristicType.ActionPoints, new BaseCharacteristic(parseShort(points[i].split(";")[0])));
            characteristics.put(CharacteristicType.MovementPoints, new BaseCharacteristic(parseShort(points[i].split(";")[1])));

            short level = parseShort(grade[i].split("@")[0]);
            monsters.put(level, new Monster(this, ((byte) (i + 1)), level, Integer.parseInt(experience[i]), grade[i], life[i], statistics[i], characteristics, getSpells(i, spells, entityFactory)));
        });

        return monsters;
    }

    private List<Spell> getSpells(int grade, String[] spellData, EntityFactory entityFactory) {
        List<Spell> spells = new ArrayList<>();

        if (spellData[0].isEmpty())
            return spells;

        String data = (grade >= spellData.length ? spellData[spellData.length - 1] : spellData[grade]);

        for (String spell : data.split(";")) {
            if (spell.isEmpty())
                continue;

            String[] information = spell.split("@");
            spells.add(entityFactory.getSpellTemplate(Short.parseShort(information[0])).getLevel(Byte.parseByte(information[1])));
        }
        return spells;
    }

    public Monster getByLevel(short level) {
        return this.monsters.get(level);
    }

    public Monster getRandom() {
        return new ArrayList<>(this.monsters.values()).get(new Random().nextInt(this.monsters.size()));
    }

    public Monster getRandom(short minimum, short maximum) {
        Optional<Monster> optional = new ArrayList<>(this.monsters.values()).stream().filter(monster -> monster.getLevel() <= maximum && monster.getLevel() >= minimum).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

}
