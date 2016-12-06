package org.graviton.game.creature.monster;

import lombok.Data;
import org.graviton.game.statistics.MonsterStatistics;
import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;

import java.util.Map;

/**
 * Created by Botan on 02/12/2016. 21:34
 */
@Data
public class Monster {
    private final MonsterTemplate template;
    private final MonsterStatistics statistics;

    private final byte size;
    private final byte grade;
    private final int baseExperience;

    public Monster(MonsterTemplate template, byte grade, short level, int baseExperience, String resistance, String life, String statistics, Map<CharacteristicType, Characteristic> characteristics) {
        this.template = template;
        this.size = (byte) (100 + (2 * (grade - 1)));
        this.grade = grade;
        this.baseExperience = baseExperience != 0 ? baseExperience : 10;
        this.statistics = new MonsterStatistics(level, life, resistance.split("@")[1].split(";"), statistics.split(","), characteristics);
    }

    public short getLevel() {
        return this.statistics.getLevel();
    }
}
