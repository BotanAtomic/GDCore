package org.graviton.game.items.template;

import javafx.util.Pair;
import lombok.Data;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.action.Action;
import org.graviton.game.filter.ConditionList;
import org.graviton.game.items.Item;
import org.graviton.game.items.Panoply;
import org.graviton.game.items.common.Bonus;
import org.graviton.game.items.common.ItemEffect;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.game.items.common.ItemType;
import org.graviton.game.items.weapon.WeaponEffect;
import org.graviton.network.game.GameClient;
import org.graviton.xml.XMLElement;

import java.util.*;

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
    private final ConditionList conditionList;
    private final List<Pair<Action,String>> actions = new LinkedList<>();
    private byte actionPointCost;
    private byte[] scopeRange;
    private short criticalRate, criticalBonus, failureRate;
    private boolean twoHands;

    private TreeMap<ItemEffect, Bonus> effects = new TreeMap<>();
    private String baseEffects;

    private Collection<WeaponEffect> weaponEffects;

    private Panoply panoply;

    public ItemTemplate(XMLElement element) {
        this.id = element.getAttribute("id").toShort();
        this.type = ItemType.get(element.getAttribute("type").toByte());
        this.level = element.getAttribute("level").toShort();
        this.pods = element.getAttribute("weight").toShort();
        this.price = element.getAttribute("price").toInt();
        this.baseEffects = element.getAttribute("stats").toString();

        this.conditionList = new ConditionList(element.getElementByTagName("conditions").toString());

        element.getElementsByTagName("effect").forEach(effect -> this.effects.put(ItemEffect.get(effect.getAttribute("type").toShort()),
                Bonus.parseBonus(effect.getAttribute("bonus").toString())));

        if (this.type != null && this.type.isWeapon()) {
            this.weaponEffects = buildSpellEffects();
            this.actionPointCost = element.getAttribute("cost").toByte();
            scopeRange = new byte[]{element.getAttribute("minRange").toByte(), element.getAttribute("maxRange").toByte()};
            criticalRate = element.getAttribute("criticalRate").toShort();
            failureRate = element.getAttribute("failureRate").toShort();
            criticalBonus = element.getAttribute("criticalBonus").toShort();
            twoHands = element.getAttribute("twoHands").toBoolean();
        }
    }

    private Collection<WeaponEffect> buildSpellEffects() {
        Collection<WeaponEffect> weaponEffects = new ArrayList<>();
        this.effects.forEach((itemEffect, bonus) -> {
            if (itemEffect.weaponEffect() != null)
                weaponEffects.add(itemEffect.weaponEffect().setBonus(bonus));

        });
        return weaponEffects;
    }

    public void addAction(Action itemAction, String parameter) {
        this.actions.add(new Pair<>(itemAction, parameter));
    }

    public void applyAction(GameClient client) {
        this.actions.forEach(pair -> pair.getKey().apply(client, pair.getValue()));
    }

    public Item createRandom(EntityFactory entityFactory) {
        return createRandom(entityFactory.getNextItemId());
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

    public TreeMap<ItemEffect, Short> getEffectByString(String data) {
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
                short value = maximum > 0 ? maximum : Short.parseShort(arguments[1], 16);
                effects.put(ItemEffect.get(statisticId), value);
            }
        }
        return effects;
    }

    public String parse() {
        return this.id + this.baseEffects + ";" + this.price;
    }
}
