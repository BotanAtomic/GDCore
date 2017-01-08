package org.graviton.game.interaction;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.interaction.actions.*;
import org.graviton.network.game.GameClient;

import java.util.ArrayDeque;


/**
 * Created by Botan on 16/11/2016 : 21:03
 */
@Slf4j
public class InteractionManager extends ArrayDeque<AbstractGameAction> {
    private final GameClient client;

    private int interactionCreature;

    public InteractionManager(GameClient gameClient) {
        this.client = gameClient;
    }

    public void create(short id, String data) {
        InteractionType interactionType = InteractionType.get(id);

        if (interactionType == null)
            interactionType = InteractionType.UNKNOWN;

        switch (interactionType) {
            case MOVEMENT:
                addAction(client.getPlayer().getFight() == null ? new PlayerMovement(client.getPlayer(), data) : new FightMovement(client.getPlayer(), data));
                break;

            case SPELL_ATTACK:
                addAction(new SpellAttack(client.getPlayer(), new short[]{Short.parseShort(data.split(";")[0]), Short.parseShort(data.split(";")[1])}));
                break;

            case ASK_DEFY:
                addAction(new AskDefy(client, Integer.parseInt(data)));
                break;

            case ACCEPT_DEFY:
                addAction(new AcceptDefy(client, interactionCreature, client.getPlayer().getGameMap()));
                break;

            case CANCEL_DEFY:
                addAction(new CancelDefy(client, interactionCreature));
                break;

            case JOIN_FIGHT:
                addAction(new JoinFight(client, data.split(";")));
                break;

            default:
                log.error("not implemented game action : {}", id);
        }
    }


    public void end(AbstractGameAction gameAction, boolean success, String data) {
        if (gameAction != null) {
            if (!success)
                gameAction.cancel(data);
            else
                gameAction.finish(data);
        }
    }

    private void addAction(AbstractGameAction gameAction) {
        if (gameAction.begin())
            super.add(gameAction);
    }

    public void setInteractionWith(int creature) {
        this.interactionCreature = creature;
    }

}
