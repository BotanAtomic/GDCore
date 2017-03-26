package org.graviton.game.statistics.type;

import org.graviton.game.guild.Guild;
import org.graviton.game.statistics.BaseCharacteristic;
import org.graviton.game.statistics.Life;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.common.Statistics;
import org.jooq.Record;

import static org.graviton.database.jooq.game.tables.Guilds.GUILDS;

/**
 * Created by Botan on 05/03/2017. 17:22
 */
public class GuildStatistics extends Statistics {
    private final Guild guild;

    public GuildStatistics(Guild guild, Record record) {
        this.guild = guild;
        super.initialize();

        super.put(CharacteristicType.Pods, new BaseCharacteristic((short) 1000));
        super.put(CharacteristicType.Prospection, new BaseCharacteristic((short) 100));
        super.put(CharacteristicType.Wisdom, new BaseCharacteristic((short) 100));

        super.put(CharacteristicType.Strength, new BaseCharacteristic(guild.getLevel()));
        super.put(CharacteristicType.Intelligence, new BaseCharacteristic(guild.getLevel()));
        super.put(CharacteristicType.Chance, new BaseCharacteristic(guild.getLevel()));
        super.put(CharacteristicType.Agility, new BaseCharacteristic(guild.getLevel()));
        super.put(CharacteristicType.Damage, new BaseCharacteristic(guild.getLevel()));

        super.put(CharacteristicType.Life, new BaseCharacteristic((short) (guild.getLevel() * 100)));

        if (guild.getLevel() > 1) {
            super.put(CharacteristicType.ResistancePercentEarth, new BaseCharacteristic((short) Math.floor(guild.getLevel() / 2)));
            super.put(CharacteristicType.ResistancePercentWind, new BaseCharacteristic((short) Math.floor(guild.getLevel() / 2)));
            super.put(CharacteristicType.ResistancePercentNeutral, new BaseCharacteristic((short) Math.floor(guild.getLevel() / 2)));
            super.put(CharacteristicType.ResistancePercentWater, new BaseCharacteristic((short) Math.floor(guild.getLevel() / 2)));
            super.put(CharacteristicType.ResistancePercentFire, new BaseCharacteristic((short) Math.floor(guild.getLevel() / 2)));
            super.put(CharacteristicType.DodgeMovementPoints, new BaseCharacteristic((short) Math.floor(guild.getLevel() / 2)));
            super.put(CharacteristicType.DodgeActionPoints, new BaseCharacteristic((short) Math.floor(guild.getLevel() / 2)));
        }

        if(record != null) {
            get(CharacteristicType.Prospection).addContext(record.get(GUILDS.PROSPECTION));
            get(CharacteristicType.Pods).addContext(record.get(GUILDS.PODS));
            get(CharacteristicType.Wisdom).addContext(record.get(GUILDS.WISDOM));
            guild.setMaxCollector(record.get(GUILDS.LIMIT));
        }

    }

    @Override
    public Statistics copy() {
        return null;
    }

    @Override
    public Life getLife() {
        return null;
    }
}
