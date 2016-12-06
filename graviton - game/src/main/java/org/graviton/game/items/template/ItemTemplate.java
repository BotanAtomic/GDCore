package org.graviton.game.items.template;

import lombok.Data;
import org.graviton.game.items.Item;
import org.graviton.game.items.common.Bonus;
import org.graviton.game.items.common.ItemEffect;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.game.items.common.ItemType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.TreeMap;
import java.util.stream.IntStream;


/**
 * Created by Botan on 03/12/2016. 20:23
 */

@Data
public class ItemTemplate {
    private final short id;
    private final ItemType type;
    private final short level;
    private final short pods;
    private final int price;
    private final String condition;

    private byte actionPointCost;
    private byte[] scopeRange;
    private short criticalRate, criticalBonus, failureRate;
    private boolean twoHands;

    private TreeMap<ItemEffect, Bonus> effects = new TreeMap<>();

    public ItemTemplate(Element element) {
        this.id = Short.parseShort(element.getAttribute("id"));
        this.type = ItemType.get(Byte.parseByte(element.getAttribute("type")));
        this.level = Short.parseShort(element.getAttribute("level"));
        this.pods = Short.parseShort(element.getAttribute("weight"));
        this.price = Short.parseShort(element.getAttribute("price"));
        this.condition = element.getElementsByTagName("conditions").item(0).getTextContent();

        NodeList nodeList = element.getElementsByTagName("effect");
        IntStream.range(0, nodeList.getLength()).forEach(i -> {
            Element effect = (Element) nodeList.item(i);
            this.effects.put(ItemEffect.get(Short.parseShort(effect.getAttribute("type"))), Bonus.parseBonus(effect.getAttribute("bonus")));
        });

        if (this.type.isWeapon()) {
            this.actionPointCost = Byte.parseByte(element.getAttribute("cost"));
            scopeRange = new byte[]{Byte.parseByte(element.getAttribute("minRange")), Byte.parseByte(element.getAttribute("maxRange"))};
            criticalRate = Short.parseShort(element.getAttribute("criticalRate"));
            failureRate = Short.parseShort(element.getAttribute("failureRate"));
            criticalBonus = Short.parseShort(element.getAttribute("criticalBonus"));
            twoHands = Boolean.parseBoolean(element.getAttribute("twoHands"));
        }
    }

    public Item createRandom(int nextId) {
        return new Item(nextId, this, ItemPosition.NotEquipped, generate(false));
    }

    public Item createMax(int nextId) {
        return new Item(nextId, this, ItemPosition.NotEquipped, generate(true));
    }

    private TreeMap<ItemEffect, Short> generate(boolean max) {
        return new TreeMap<ItemEffect, Short>() {{
            effects.forEach((key, value) -> put(key, max ? value.max() : value.random()));
        }};
    }

    public TreeMap<ItemEffect, Short> getEffectByString(String data, boolean useMax) {
        TreeMap<ItemEffect, Short> effects = new TreeMap<>();
        if (data.isEmpty()) return effects;

        short maximum;
        for (String statisticTemplate : data.split(",")) {
            String[] arguments = statisticTemplate.split("#");
            final short statisticId = Short.parseShort(arguments[0], 16);

            if (arguments.length < 5)
                continue;

            String argument = arguments[4];

            if (argument.contains("d") && argument.contains("+")) {
                maximum = Short.parseShort(arguments[2], 16);
                short value = useMax ? maximum > 0 ? maximum : Short.parseShort(arguments[1], 16) : this.getRandomJet(argument);
                effects.put(ItemEffect.get(statisticId), value);
            }
        }
        return effects;
    }

    private short getRandomJet(String jet) {
        short faces = Short.parseShort(jet.split("d")[1].split("\\+")[0]);
        final short[] number = {(short) (Short.parseShort(jet.split("d")[1].split("\\+")[1]) + (int) (Math.random() + faces))};
        IntStream.range(1, Short.parseShort(jet.split("d")[0])).forEach(i -> number[0] += (int) (Math.random() * faces));
        return number[0];
    }

}
