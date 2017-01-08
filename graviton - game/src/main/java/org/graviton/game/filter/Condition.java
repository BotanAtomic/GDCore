package org.graviton.game.filter;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.game.filter.enums.ConditionType;
import org.graviton.game.filter.enums.FilterType;

/**
 * Created by Botan on 27/12/2016. 14:30
 */
@Data
public class Condition {
    private ConditionType conditionType;
    private FilterType filterType;
    private String value;
    private boolean isOptional;

    static Condition parse(String data, boolean isOptional) {
        Condition condition = new Condition();

        condition.filterType = FilterType.get(data.charAt(2));
        condition.conditionType = ConditionType.get(data.substring(0, 2));
        condition.value = data.substring(3);
        condition.isOptional = isOptional;

        return condition;
    }

    boolean check(Player player) {
        return conditionType.check(player, filterType, value);
    }

}
