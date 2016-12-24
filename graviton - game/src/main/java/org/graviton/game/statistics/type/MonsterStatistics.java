package org.graviton.game.statistics.type;

import com.google.common.collect.Maps;
import lombok.Data;
import org.graviton.game.statistics.BaseCharacteristic;
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
        this.characteristics.put(CharacteristicType.ResistancePercentNeutral, new BaseCharacteristic(Short.parseShort(resistance[0])));
        this.characteristics.put(CharacteristicType.ResistancePercentEarth, new BaseCharacteristic(Short.parseShort(resistance[1])));
        this.characteristics.put(CharacteristicType.ResistancePercentFire, new BaseCharacteristic(Short.parseShort(resistance[2])));
        this.characteristics.put(CharacteristicType.ResistancePercentWater, new BaseCharacteristic(Short.parseShort(resistance[3])));
        this.characteristics.put(CharacteristicType.ResistancePercentWind, new BaseCharacteristic(Short.parseShort(resistance[4])));
        this.characteristics.put(CharacteristicType.DodgeActionPoints, new BaseCharacteristic(Short.parseShort(resistance[5])));
        this.characteristics.put(CharacteristicType.DodgeMovementPoints, new BaseCharacteristic(Short.parseShort(resistance[6])));


        this.characteristics.put(CharacteristicType.Strength, new BaseCharacteristic(Short.parseShort(statistics[0])));
        this.characteristics.put(CharacteristicType.Wisdom, new BaseCharacteristic(Short.parseShort(statistics[1])));
        this.characteristics.put(CharacteristicType.Intelligence, new BaseCharacteristic(Short.parseShort(statistics[2])));
        this.characteristics.put(CharacteristicType.Chance, new BaseCharacteristic(Short.parseShort(statistics[3])));
        this.characteristics.put(CharacteristicType.Agility, new BaseCharacteristic(Short.parseShort(statistics[4])));
    }

}
