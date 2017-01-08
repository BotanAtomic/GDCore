package org.graviton.game.filter.type;

import org.graviton.game.client.player.Player;
import org.graviton.game.filter.Filter;
import org.graviton.game.filter.enums.FilterType;
import org.graviton.game.statistics.common.CharacteristicType;

/**
 * Created by Botan on 27/12/2016. 17:14
 */
public class StatisticsFilter implements Filter {
    private final CharacteristicType characteristicType;
    private final boolean total;

    public StatisticsFilter(CharacteristicType characteristicType, boolean total) {
        this.characteristicType = characteristicType;
        this.total = total;
    }

    @Override
    public boolean check(Player player, FilterType filterType, String data) {
        int value = total ? player.getStatistics().get(characteristicType).total() : player.getStatistics().get(characteristicType).base();
        int required = Integer.parseInt(data);
        switch (filterType) {
            case EQUALS:
                return required == value;
            case LESS:
                return required > value;
            case MORE:
                return required < value;
        }
        return false;
    }
}
