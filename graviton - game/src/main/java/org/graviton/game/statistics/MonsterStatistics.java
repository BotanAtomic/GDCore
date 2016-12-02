package org.graviton.game.statistics;

import com.google.common.collect.Maps;
import lombok.Data;
import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;

import java.util.Map;

/**
 * Created by Botan on 02/12/2016. 21:37
 */
@Data
public class MonsterStatistics {
    private final short level;
    private final Map<CharacteristicType, Characteristic> characteristics = Maps.newHashMap();
    //current -> max
    private int life;

    public MonsterStatistics(short level, String life, String[] resistance, String[] statistics, Map<CharacteristicType, Characteristic> characteristics) {
        this.level = level;
        this.life = Integer.parseInt(life);

        this.characteristics.putAll(characteristics);
        this.characteristics.put(CharacteristicType.ResistancePercentNeutral, new Characteristic(Short.parseShort(resistance[0])));
        this.characteristics.put(CharacteristicType.ResistancePercentEarth, new Characteristic(Short.parseShort(resistance[1])));
        this.characteristics.put(CharacteristicType.ResistancePercentFire, new Characteristic(Short.parseShort(resistance[2])));
        this.characteristics.put(CharacteristicType.ResistancePercentWater, new Characteristic(Short.parseShort(resistance[3])));
        this.characteristics.put(CharacteristicType.ResistancePercentWind, new Characteristic(Short.parseShort(resistance[4])));
        this.characteristics.put(CharacteristicType.DodgeActionPoints, new Characteristic(Short.parseShort(resistance[5])));
        this.characteristics.put(CharacteristicType.DodgeMovementPoints, new Characteristic(Short.parseShort(resistance[6])));


        this.characteristics.put(CharacteristicType.Strength, new Characteristic(Short.parseShort(statistics[0])));
        this.characteristics.put(CharacteristicType.Wisdom, new Characteristic(Short.parseShort(statistics[1])));
        this.characteristics.put(CharacteristicType.Intelligence, new Characteristic(Short.parseShort(statistics[2])));
        this.characteristics.put(CharacteristicType.Chance, new Characteristic(Short.parseShort(statistics[3])));
        this.characteristics.put(CharacteristicType.Agility, new Characteristic(Short.parseShort(statistics[4])));
    }

}
