package org.graviton.game.interaction;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.interaction.actions.AbstractGameAction;
import org.graviton.game.interaction.actions.PlayerMovement;
import org.graviton.network.game.GameClient;

import java.util.ArrayDeque;


/**
 * Created by Botan on 16/11/2016 : 21:03
 */
@Slf4j
public class InteractionManager extends ArrayDeque<AbstractGameAction> {
    private final GameClient client;

    public InteractionManager(GameClient gameClient) {
        this.client = gameClient;
    }

    public void create(short id, String data) {
        InteractionType interactionType = InteractionType.get(id);

        if (interactionType == null)
            interactionType = InteractionType.UNKNOWN;

        switch (interactionType) {
            case MOVEMENT:
                addAction(new PlayerMovement(client.getPlayer(), data));
                break;
            default:
                log.error("not implemented game action : {}", id);
        }
    }


    public void end(AbstractGameAction gameAction, boolean success, String data) {
        if (!success)
            gameAction.cancel(data);
        else
            gameAction.finish(data);
    }

    private void addAction(AbstractGameAction gameAction) {
        super.add(gameAction);
        if (!gameAction.begin())
            super.remove(gameAction);
    }
}
