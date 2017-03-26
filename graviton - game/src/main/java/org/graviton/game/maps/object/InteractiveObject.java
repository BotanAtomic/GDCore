package org.graviton.game.maps.object;

import lombok.Data;
import org.graviton.game.maps.GameMap;
import org.graviton.network.game.protocol.GamePacketFormatter;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Botan on 22/03/2017. 18:40
 */

@Data
public class InteractiveObject {
    private final ScheduledExecutorService scheduledExecutorService;
    private final InteractiveObjectTemplate template;
    private final GameMap gameMap;
    private final short cell;

    private boolean interactive;
    private InteractiveObjectState state = InteractiveObjectState.FULL;

    InteractiveObject(InteractiveObjectTemplate template, GameMap gameMap, short cell, ScheduledExecutorService executorService) {
        this.scheduledExecutorService = executorService;
        this.template = template;
        this.interactive = true;
        this.gameMap = gameMap;
        this.cell = cell;
    }

    public void use() {
        this.interactive = false;
        this.state = InteractiveObjectState.EMPTYING;
        gameMap.send(getData());

        if (template.getRespawnTime() > 0) {
            scheduledExecutorService.schedule(() -> {
                if (state == InteractiveObjectState.FULL)
                    return;

                this.state = InteractiveObjectState.FULLING;
                this.interactive = true;
                gameMap.send(getData());
                this.state = InteractiveObjectState.FULL;
            }, template.getRespawnTime(), TimeUnit.MILLISECONDS);
        }
    }

    public String getData() {
        return GamePacketFormatter.interactiveObjectMessage(cell, this.state.id(), this.interactive);
    }

}
