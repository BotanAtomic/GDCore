package org.graviton.game.filter.type;

import org.graviton.game.client.player.Player;
import org.graviton.game.filter.Filter;
import org.graviton.game.filter.enums.FilterType;
import org.graviton.game.quest.Quest;

/**
 * Created by Botan on 14/07/17. 14:05
 */
public class QuestFinishFilter implements Filter {

    @Override
    public boolean check(Player player, FilterType filterType, String data) {
        Quest quest = player.getQuest(Short.parseShort(data));

        switch (filterType) {
            case DIFFERENT:
                return quest == null || !quest.isFinish();

            case EQUALS:
                return quest != null && quest.isFinish();
        }
        return false;
    }

}
