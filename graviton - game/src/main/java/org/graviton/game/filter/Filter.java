package org.graviton.game.filter;

import org.graviton.game.client.player.Player;
import org.graviton.game.filter.enums.FilterType;

/**
 * Created by Botan on 27/12/2016. 14:30
 */
public interface Filter {
    boolean check(Player player, FilterType filterType, String data);
}
