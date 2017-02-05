package org.graviton.game.statistics.type;


import javafx.util.Pair;
import lombok.Data;
import org.graviton.collection.CollectionQuery;
import org.graviton.converter.Converters;
import org.graviton.game.client.player.Player;
import org.graviton.game.items.Item;
import org.graviton.game.items.Panoply;
import org.graviton.game.items.common.ItemEffect;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.game.statistics.*;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.common.Statistics;
import org.graviton.network.game.protocol.ItemPacketFormatter;
import org.jooq.Record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.graviton.database.jooq.login.tables.Players.PLAYERS;

/**
 * Created by Botan on 11/11/2016 : 21:12
 */

@Data
public class PlayerStatistics extends Statistics {
    private final Player player;

    //current -> max
    private short[] pods;

    private Life life;

    private short statisticPoints, spellPoints, energy, level;
    private long experience;

    public PlayerStatistics(Player player, Record record, byte prospection) {
        this.player = player;
        this.statisticPoints = record.get(PLAYERS.STAT_POINTS);
        this.spellPoints = record.get(PLAYERS.SPELL_POINTS);
        this.energy = record.get(PLAYERS.ENERGY);
        this.level = record.get(PLAYERS.LEVEL);
        this.experience = record.get(PLAYERS.EXPERIENCE);
        this.life = new Life(this, 55 + ((level - 1) * 5), 55 + ((level - 1) * 5));

        super.initialize();

        short vitality = record.get(PLAYERS.VITALITY);
        life.add(vitality);

        put(CharacteristicType.Vitality, new BaseCharacteristic(vitality));
        put(CharacteristicType.Wisdom, new BaseCharacteristic(record.get(PLAYERS.WISDOM)));
        put(CharacteristicType.Strength, new BaseCharacteristic(record.get(PLAYERS.STRENGTH)));
        put(CharacteristicType.Intelligence, new BaseCharacteristic(record.get(PLAYERS.INTELLIGENCE)));
        put(CharacteristicType.Chance, new BaseCharacteristic(record.get(PLAYERS.CHANCE)));
        put(CharacteristicType.Agility, new BaseCharacteristic(record.get(PLAYERS.AGILITY)));
        put(CharacteristicType.ActionPoints, new BaseCharacteristic((short) (level == 200 ? 8 : level >= 100 ? 7 : 6)));
        put(CharacteristicType.MovementPoints, new BaseCharacteristic((short) 3));
        put(CharacteristicType.Prospection, new BaseCharacteristic(prospection));
        put(CharacteristicType.Initiative, new Initiative(this, (short) 0));
        put(CharacteristicType.DodgeActionPoints, new Dodge(this));
        put(CharacteristicType.DodgeMovementPoints, new Dodge(this));
        put(CharacteristicType.CriticalHit, new CriticalRate(this));
        put(CharacteristicType.Summons, new BaseCharacteristic((short) 1));

        applyItemEffects();
        applyPanoplyEffect();

        refreshPods();
    }

    public PlayerStatistics(Player player, byte prospection) {
        this.player = player;
        this.statisticPoints = 0;
        this.spellPoints = 0;
        this.energy = 10000;
        this.level = 1;
        this.experience = 0;
        this.life = new Life(this, 55, 55);
        this.pods = new short[]{0, 1000};

        super.initialize();

        put(CharacteristicType.ActionPoints, new BaseCharacteristic((short) 6));
        put(CharacteristicType.MovementPoints, new BaseCharacteristic((short) 3));
        put(CharacteristicType.Prospection, new BaseCharacteristic(prospection));
        put(CharacteristicType.Initiative, new Initiative(this, (short) 0));
        put(CharacteristicType.DodgeActionPoints, new Dodge(this));
        put(CharacteristicType.DodgeMovementPoints, new Dodge(this));
        put(CharacteristicType.CriticalHit, new CriticalRate(this));
        put(CharacteristicType.Summons, new BaseCharacteristic((short) 1));
    }

    private void applyItemEffects() {
        player.getInventory().values().stream().filter(item -> item.getPosition().equipped()).forEach(this::applyItemEffects);
    }

    public void applyItemEffects(Item item) {
        item.getStatistics().forEach(((itemEffect, value) -> {
            ItemPosition position = item.getPosition();
            Pair<CharacteristicType, Boolean> values = itemEffect.convert();
            boolean equipped = position.equipped();

            if (values.getKey() != null) {
                if (values.getValue())
                    get(values.getKey()).addEquipment(!equipped ? (short) -value : value);
                else
                    get(values.getKey()).addEquipment(equipped ? (short) -value : value);
            }

            if (values.getKey() == CharacteristicType.Life || values.getKey() == CharacteristicType.Vitality)
                life.add(equipped ? value : -value);
        }));
    }

    private void applyPanoplyEffect() {
        player.getInventory().getEquippedItems().stream().filter(item -> item.getTemplate().getPanoply() != null).
                collect(Collectors.toList()).forEach(item -> applyPanoplyEffect(item.getTemplate().getPanoply(), true));
    }

    public void applyPanoplyEffect(Panoply panoply, boolean equippedItem) {
        Collection<Item> equippedItems = player.getInventory().getEquippedItems();

        byte equipped = panoply.getEquippedObject(player.getInventory().getEquippedItems().stream().filter(item -> item.getTemplate().getPanoply() != null).collect(Collectors.toList()));

        Map<ItemEffect, Short> effects = panoply.effects(equipped);

        clearPanoplyEffect(panoply, (byte) (equippedItem ? equipped - 1 : equipped + 1));

        if (effects != null) {
            effects.forEach(((itemEffect, value) -> {
                Pair<CharacteristicType, Boolean> values = itemEffect.convert();
                get(values.getKey()).addEquipment(value);
            }));
        }

        player.send(ItemPacketFormatter.panoplyMessage(panoply, effects, equipped, CollectionQuery.from(equippedItems).transform(Converters.ITEM_TO_ID).computeList(new ArrayList<>())));
    }

    private void clearPanoplyEffect(Panoply panoply, byte equipped) {
        if (equipped >= 2)
            panoply.effects(equipped).forEach((effect, value) -> {
                Pair<CharacteristicType, Boolean> values = effect.convert();
                get(values.getKey()).addEquipment((short) -value);
            });
    }

    public short getProspection() {
        return (short) (get(CharacteristicType.Prospection).total() + get(CharacteristicType.Chance).total() / 10);
    }

    public void refreshPods() {
        AtomicInteger value = new AtomicInteger(0);
        this.player.getInventory().values().stream().filter(item -> !item.getPosition().equipped()).forEach(item -> value.addAndGet(item.getTemplate().getPods()));
        this.pods = new short[]{(short) value.get(), getMaxPods()};
    }

    private short getMaxPods() {
        return (short) (1000 + (get(CharacteristicType.Strength).total() * 5) + get(CharacteristicType.Pods).total());
    }

    public void upLevel() {
        this.spellPoints++;
        this.statisticPoints += 5;
        this.level++;
        this.experience = player.getEntityFactory().getExperience(level).getPlayer();

        this.life.addMaximum(5);
        this.life.regenMax();

        if (level % 100 == 0)
            get(CharacteristicType.ActionPoints).addBase((short) 1);
    }

    public void addExperience(long experience) {
        this.experience += experience;

        while (this.experience > player.getEntityFactory().getExperience(this.level).getNext().getPlayer())
            player.upLevel();
    }

    @Override
    public Statistics copy() {
        return null;
    }
}
