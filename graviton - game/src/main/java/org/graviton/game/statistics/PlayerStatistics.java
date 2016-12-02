package org.graviton.game.statistics;


import com.google.common.collect.Maps;
import lombok.Data;
import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;
import org.jooq.Record;

import java.util.Map;

import static org.graviton.database.jooq.login.tables.Players.PLAYERS;

/**
 * Created by Botan on 11/11/2016 : 21:12
 */
@Data
public class PlayerStatistics {
    private final Map<CharacteristicType, Characteristic> characteristics = Maps.newHashMap();
    //current -> max
    private short[] life;
    private short[] pods;
    private short statisticPoints, spellPoints, energy, level;
    private long experience;

    public PlayerStatistics(Record record, byte prospection) {
        this.statisticPoints = record.get(PLAYERS.STAT_POINTS);
        this.spellPoints = record.get(PLAYERS.SPELL_POINTS);
        this.energy = record.get(PLAYERS.ENERGY);
        this.level = record.get(PLAYERS.LEVEL);
        this.experience = record.get(PLAYERS.EXPERIENCE);
        this.life = new short[]{record.get(PLAYERS.LIFE), 55}; //TODO : life
        this.pods = new short[]{0, 1000}; //TODO : pods (wait items)

        for (CharacteristicType type : CharacteristicType.values())
            this.characteristics.put(type, new Characteristic((short) 0));

        this.characteristics.put(CharacteristicType.Vitality, new Characteristic(record.get(PLAYERS.VITALITY)));
        this.characteristics.put(CharacteristicType.Wisdom, new Characteristic(record.get(PLAYERS.WISDOM)));
        this.characteristics.put(CharacteristicType.Strength, new Characteristic(record.get(PLAYERS.STRENGTH)));
        this.characteristics.put(CharacteristicType.Intelligence, new Characteristic(record.get(PLAYERS.INTELLIGENCE)));
        this.characteristics.put(CharacteristicType.Chance, new Characteristic(record.get(PLAYERS.CHANCE)));
        this.characteristics.put(CharacteristicType.Agility, new Characteristic(record.get(PLAYERS.AGILITY)));
        this.characteristics.put(CharacteristicType.ActionPoints, new Characteristic((short) (level == 200 ? 8 : level >= 100 ? 7 : 6)));
        this.characteristics.put(CharacteristicType.MovementPoints, new Characteristic((short) 3));
        this.characteristics.put(CharacteristicType.Prospection, new Characteristic(prospection));
    }

    public PlayerStatistics(byte prospection) {
        this.statisticPoints = 0;
        this.spellPoints = 0;
        this.energy = 10000;
        this.level = 1;
        this.experience = 0;
        this.life = new short[]{55, 55};
        this.pods = new short[]{0, 1000}; //TODO : pods (wait items)

        for (CharacteristicType type : CharacteristicType.values())
            this.characteristics.put(type, new Characteristic((short) 0));

        this.characteristics.put(CharacteristicType.ActionPoints, new Characteristic((short) 6));
        this.characteristics.put(CharacteristicType.MovementPoints, new Characteristic((short) 3));
        this.characteristics.put(CharacteristicType.Prospection, new Characteristic(prospection));
    }

    public Characteristic get(CharacteristicType type) {
        return this.characteristics.get(type);
    }

    public short getCurrentLife() {
        return life[0];
    }

    public short getMaxLife() {
        return life[1];
    }

    public short getInitiative() {
        short total = (short) (get(CharacteristicType.Strength).total() + get(CharacteristicType.Intelligence).total() +
                get(CharacteristicType.Chance).total() + get(CharacteristicType.Agility).total());

        total += get(CharacteristicType.Initiative).total();

        total *= (life[0] / life[1]);

        return total;
    }

    public short getProspection() {
        return (short) (get(CharacteristicType.Prospection).total() + get(CharacteristicType.Chance).total() / 10);
    }

}
