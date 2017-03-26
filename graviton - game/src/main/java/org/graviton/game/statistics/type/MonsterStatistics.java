package org.graviton.game.statistics.type;

import lombok.Data;
import org.graviton.game.creature.monster.Monster;
import org.graviton.game.statistics.*;
import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.common.Statistics;

import java.util.Map;

/**
 * Created by Botan on 02/12/2016. 21:37
 */
@Data
public class MonsterStatistics extends Statistics {
    private final short level;
    private Life life;

    public MonsterStatistics(Monster model) {
        this.level = model.getLevel();
        this.life = new Life(this, model.getLife().getSafeMaximum(), model.getLife().getSafeMaximum(), true);
    }

    public MonsterStatistics(short level, String life, String[] resistance, String[] statistics, Map<CharacteristicType, Characteristic> characteristics) {
        this.level = level;
        this.life = new Life(this, Integer.parseInt(life), Integer.parseInt(life), true);

        super.initialize();

        putAll(characteristics);
        put(CharacteristicType.ResistancePercentNeutral, new BaseCharacteristic(Short.parseShort(resistance[0])));
        put(CharacteristicType.ResistancePercentEarth, new BaseCharacteristic(Short.parseShort(resistance[1])));
        put(CharacteristicType.ResistancePercentFire, new BaseCharacteristic(Short.parseShort(resistance[2])));
        put(CharacteristicType.ResistancePercentWater, new BaseCharacteristic(Short.parseShort(resistance[3])));
        put(CharacteristicType.ResistancePercentWind, new BaseCharacteristic(Short.parseShort(resistance[4])));
        put(CharacteristicType.DodgeActionPoints, new BaseCharacteristic(Short.parseShort(resistance[5])));
        put(CharacteristicType.DodgeMovementPoints, new BaseCharacteristic(Short.parseShort(resistance[6])));


        put(CharacteristicType.Strength, new BaseCharacteristic(Short.parseShort(statistics[0])));
        put(CharacteristicType.Wisdom, new BaseCharacteristic(Short.parseShort(statistics[1])));
        put(CharacteristicType.Intelligence, new BaseCharacteristic(Short.parseShort(statistics[2])));
        put(CharacteristicType.Chance, new BaseCharacteristic(Short.parseShort(statistics[3])));
        put(CharacteristicType.Agility, new BaseCharacteristic(Short.parseShort(statistics[4])));
        put(CharacteristicType.Life, new BaseCharacteristic((short) 0));
        put(CharacteristicType.Vitality, new BaseCharacteristic((short) 0));
        put(CharacteristicType.Initiative, new Initiative(this, (short) 0));
        put(CharacteristicType.DodgeActionPoints, new Dodge(this));
        put(CharacteristicType.DodgeMovementPoints, new Dodge(this));
        put(CharacteristicType.CriticalHit, new CriticalRate(this));
    }

    private MonsterStatistics(MonsterStatistics monsterStatistics) {
        this.level = monsterStatistics.getLevel();
        this.life = new Life(this, monsterStatistics.getLife().getCurrent(), monsterStatistics.getLife().getCurrent(), true);
        putAll(monsterStatistics);
    }

    @Override
    public Statistics copy() {
        return new MonsterStatistics(this);
    }

    @Override
    public Life getLife() {
        return this.life;
    }
}
