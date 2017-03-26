package org.graviton.game.exchange;

import lombok.Data;
import org.graviton.game.client.player.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Botan on 03/03/2017. 23:30
 */

@Data
public class Exchanger {
    private final Player player;
    private boolean ready = false;

    private final Map<Integer,Short> items = new HashMap<>();
    private long kamas = 0;

    public Exchanger(Player player, Exchange exchange) {
        this.player = player;
        player.setExchange(exchange);
    }

    public void send(String data) {
        player.send(data);
    }

    public boolean toggle() {
        return (ready = !ready);
    }

}
