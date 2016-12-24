package org.graviton.game.statistics.type;


import javafx.util.Pair;
import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.game.items.Item;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.game.statistics.BaseCharacteristic;
import org.graviton.game.statistics.Initiative;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.common.Statistics;
import org.jooq.Record;

import java.util.concurrent.atomic.AtomicInteger;

import static org.graviton.database.jooq.login.tables.Players.PLAYERS;

/**
 * Created by Botan on 11/11/2016 : 21:12
 */

@Data
public class PlayerStatistics extends Statistics {
    private final Player player;

    //current -> max
    private short[] life;
    private short[] pods;

    private short statisticPoints, spellPoints, energy, level;
    private long experience;

    public PlayerStatistics(Player player, Record record, byte prospection) {
        this.player = player;
        this.statisticPoints = record.get(PLAYERS.STAT_POINTS);
        this.spellPoints = record.get(PLAYERS.SPELL_POINTS);
        this.energy = record.get(PLAYERS.ENERGY);
        this.level = record.get(PLAYERS.LEVEL);
        this.experience = record.get(PLAYERS.EXPERIENCE);
        this.life = new short[]{55, 55}; //TODO : life

        for (CharacteristicType type : CharacteristicType.values())
            put(type, new BaseCharacteristic((short) 0));

        put(CharacteristicType.Vitality, new BaseCharacteristic(record.get(PLAYERS.VITALITY)));
        put(CharacteristicType.Wisdom, new BaseCharacteristic(record.get(PLAYERS.WISDOM)));
        put(CharacteristicType.Strength, new BaseCharacteristic(record.get(PLAYERS.STRENGTH)));
        put(CharacteristicType.Intelligence, new BaseCharacteristic(record.get(PLAYERS.INTELLIGENCE)));
        put(CharacteristicType.Chance, new BaseCharacteristic(record.get(PLAYERS.CHANCE)));
        put(CharacteristicType.Agility, new BaseCharacteristic(record.get(PLAYERS.AGILITY)));
        put(CharacteristicType.ActionPoints, new BaseCharacteristic((short) (level == 200 ? 8 : level >= 100 ? 7 : 6)));
        put(CharacteristicType.MovementPoints, new BaseCharacteristic((short) 3));
        put(CharacteristicType.Prospection, new BaseCharacteristic(prospection));
        put(CharacteristicType.Initiative, new Initiative(this, (short) 0));

        applyItemEffects();
        refreshPods();
    }

    public PlayerStatistics(Player player, byte prospection) {
        this.player = player;
        this.statisticPoints = 0;
        this.spellPoints = 0;
        this.energy = 10000;
        this.level = 1;
        this.experience = 0;
        this.life = new short[]{55, 55};
        this.pods = new short[]{0, 1000};

        for (CharacteristicType type : CharacteristicType.values())
            put(type, new BaseCharacteristic((short) 0));

        put(CharacteristicType.ActionPoints, new BaseCharacteristic((short) 6));
        put(CharacteristicType.MovementPoints, new BaseCharacteristic((short) 3));
        put(CharacteristicType.Prospection, new BaseCharacteristic(prospection));
        put(CharacteristicType.Initiative, new Initiative(this, (short) 0));

    }

    private void applyItemEffects() {
        player.getInventory().getItems().values().stream().filter(item -> item.getPosition().equipped()).forEach(this::applyItemEffects);
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
        }));
    }

    @Override
    public short getCurrentLife() {
        return life[0];
    }

    @Override
    public short getMaxLife() {
        return life[1];
    }

    public short getProspection() {
        return (short) (get(CharacteristicType.Prospection).total() + get(CharacteristicType.Chance).total() / 10);
    }

    public void refreshPods() {
        AtomicInteger value = new AtomicInteger(0);
        this.player.getInventory().getItems().values().stream().filter(item -> !item.getPosition().equipped()).forEach(item -> value.addAndGet(item.getTemplate().getPods()));
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
    }
}
