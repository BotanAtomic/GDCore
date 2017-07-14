package org.graviton.game.filter.type;

import org.graviton.game.client.player.Player;
import org.graviton.game.filter.Filter;
import org.graviton.game.filter.enums.FilterType;

/**
 * Created by Botan on 14/07/17. 01:15
 */
public class QuestFilter implements Filter {

    @Override
    public boolean check(Player player, FilterType filterType, String data) {
        short quest = Short.parseShort(data);
        switch (filterType) {
            case EQUALS:
                return player.getQuest(quest) != null;
            case DIFFERENT:
                return player.getQuest(quest) == null;
        }
        return false;
    }
}

