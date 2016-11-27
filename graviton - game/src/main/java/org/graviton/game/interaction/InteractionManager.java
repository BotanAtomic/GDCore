package org.graviton.game.interaction;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.interaction.actions.AbstractGameAction;
import org.graviton.game.interaction.actions.PlayerMovement;
import org.graviton.network.game.GameClient;

import java.util.concurrent.LinkedBlockingDeque;


/**
 * Created by Botan on 16/11/2016 : 21:03
 */
@Slf4j
public class InteractionManager extends LinkedBlockingDeque<AbstractGameAction> {
    private final GameClient client;
    private boolean isAway;

    public InteractionManager(GameClient gameClient) {
        this.client = gameClient;
        this.isAway = false;
    }

    public void create(short id, String data) {
        switch (id) {
            case 1: //InteractionType.MOVEMENT
                addAction(new PlayerMovement(client.getPlayer(), data));
                break;
            default:
                log.error("not implemented game action : {}", id);
        }
    }


    public void end(AbstractGameAction gameAction, boolean success, String data) {
        if (gameAction != null) {
            if (success)
                gameAction.finish(data);
            else
                gameAction.cancel(data);
        }
    }

    private void addAction(AbstractGameAction gameAction) {
        super.add(gameAction);
        if (!gameAction.begin())
            super.remove(gameAction);
    }
}
