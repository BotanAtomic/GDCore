package org.graviton.game.items.template;

import lombok.Data;
import org.graviton.game.items.Item;
import org.graviton.game.items.common.Bonus;
import org.graviton.game.items.common.ItemEffect;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.game.items.common.ItemType;
import org.graviton.xml.XMLElement;

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

    public ItemTemplate(XMLElement element) {
        this.id = element.getAttribute("id").toShort();
        this.type = ItemType.get(element.getAttribute("type").toByte());
        this.level = element.getAttribute("level").toShort();
        this.pods = element.getAttribute("weight").toShort();
        this.price = element.getAttribute("price").toInt();
        this.condition = element.getElementByTagName("conditions").toString();

        element.getElementsByTagName("effect").forEach(effect ->
                this.effects.put(ItemEffect.get(effect.getAttribute("type").toShort()),
                        Bonus.parseBonus(effect.getAttribute("bonus").toString())));

        if (this.type != null && this.type.isWeapon()) {
            this.actionPointCost = element.getAttribute("cost").toByte();
            scopeRange = new byte[]{element.getAttribute("minRange").toByte(), element.getAttribute("maxRange").toByte()};
            criticalRate = element.getAttribute("criticalRate").toShort();
            failureRate = element.getAttribute("failureRate").toShort();
            criticalBonus = element.getAttribute("criticalBonus").toShort();
            twoHands = element.getAttribute("twoHands").toBoolean();
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
