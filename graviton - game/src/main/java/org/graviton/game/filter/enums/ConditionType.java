package org.graviton.game.filter.enums;


import org.graviton.game.client.player.Player;
import org.graviton.game.filter.Filter;
import org.graviton.game.filter.type.StatisticsFilter;
import org.graviton.game.statistics.common.CharacteristicType;

/**
 * Created by Botan on 27/12/2016. 14:40
 */
public enum ConditionType {
    HAVE_ITEM("PO"),
    NAME("PN"),
    JOB_LEVEl("PJ"),
    ACTIVE_QUEST("Qa"),
    JOB("Pj"),

    TOTAL_INTELLIGENCE("CI", new StatisticsFilter(CharacteristicType.Intelligence, true)),
    TOTAL_STRENGTH("CS", new StatisticsFilter(CharacteristicType.Strength, true)),
    TOTAL_VITALITY("CV", new StatisticsFilter(CharacteristicType.Vitality, true)),
    TOTAL_AGILITY("CA", new StatisticsFilter(CharacteristicType.Agility, true)),
    TOTAL_CHANCE("CC", new StatisticsFilter(CharacteristicType.Chance, true)),
    TOTAL_WISDOM("CW", new StatisticsFilter(CharacteristicType.Wisdom, true)),
    TOTAL_MOVEMENT_POINT("CM", new StatisticsFilter(CharacteristicType.MovementPoints, true)),

    INTELLIGENCE("Ci", new StatisticsFilter(CharacteristicType.Intelligence, false)),
    STRENGTH("Cs", new StatisticsFilter(CharacteristicType.Strength, false)),
    VITALITY("Cv", new StatisticsFilter(CharacteristicType.Vitality, false)),
    AGILITY("Ca", new StatisticsFilter(CharacteristicType.Agility, false)),
    CHANCE("Cc", new StatisticsFilter(CharacteristicType.Chance, false)),
    WISDOM("Cw", new StatisticsFilter(CharacteristicType.Wisdom, false)),

    ALIGNMENT("Ps"),
    ALIGNMENT_LEVEL("Pa"),
    ALIGNMENT_GRADE("PP"),

    LEVEL("PL"),
    KAMAS("PK"),
    BREED("PG"),
    SEX("PS"),
    SUBSCRIBE("PZ"),
    ADMIN_LEVEL("PX"),
    PODS("PW"),
    SUB_AREA("PB"),
    MARRIED("PR"),
    MAP("SI"),
    MAP_ALIGNMENT("MA"),

    EMPTY("", (player, filterType, data) -> true);

    private final String value;
    private Filter filter;

    ConditionType(String value) {
        this.value = value;
    }

    ConditionType(String value, Filter filter) {
        this.value = value;
        this.filter = filter;
    }

    public static ConditionType get(String value) {
        for (ConditionType type : values())
            if (type.value.equals(value))
                return type;
        return EMPTY;
    }

    public boolean check(Player player, FilterType filterType, String data) {
        return filter == null || filter.check(player, filterType, data);
    }


}
