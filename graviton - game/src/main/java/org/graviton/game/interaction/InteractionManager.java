package org.graviton.game.interaction;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.client.player.Player;
import org.graviton.game.fight.Fight;
import org.graviton.game.interaction.actions.AbstractGameAction;
import org.graviton.game.interaction.actions.FightMovement;
import org.graviton.game.interaction.actions.PlayerMovement;
import org.graviton.game.maps.GameMap;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;

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
                move(data);
                break;

            case ASK_DEFY:
                askDefy(data);
                break;

            case ACCEPT_DEFY:
                acceptDefy(data, (GameMap) client.getPlayer().getMap());
                break;

            case CANCEL_DEFY:
                cancelDefy(data);
                break;

            case JOIN_FIGHT:
                joinFight(data);
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
        super.add(gameAction);
        if (!gameAction.begin())
            super.remove(gameAction);
    }

    public void setInteractionWith(int creature) {
        this.interactionCreature = creature;
    }

    private int getInteractionCreature() {
        return this.interactionCreature;
    }

    private void move(String data) {
        if (client.getPlayer().getFight() == null)
            addAction(new PlayerMovement(client.getPlayer(), data));
        else {
            addAction(new FightMovement(client.getPlayer(), data));
        }
    }

    private void askDefy(String data) {
        int targetId = Integer.parseInt(data);
        Player target = client.getPlayerRepository().get(targetId);

        if (target == null) {
            client.send(GamePacketFormatter.awayPlayerMessage(targetId));
            return;
        }

        setInteractionWith(targetId);
        target.getAccount().getClient().getInteractionManager().setInteractionWith(client.getPlayer().getId());
        client.getPlayer().getMap().send(GamePacketFormatter.askDuelMessage(client.getPlayer().getId(), targetId));
    }

    private void cancelDefy(String data) {
        Player target = client.getPlayerRepository().get(client.getInteractionManager().getInteractionCreature());

        if (target == null) {
            client.send(GamePacketFormatter.awayPlayerMessage(Integer.parseInt(data)));
            return;
        }

        client.getPlayer().getMap().send(GamePacketFormatter.cancelDuelMessage(client.getPlayer().getId(), target.getId()));

        setInteractionWith(0);
        target.getAccount().getClient().getInteractionManager().setInteractionWith(0);
    }

    private void acceptDefy(String data, GameMap gameMap) {
        Player target = client.getPlayerRepository().get(client.getInteractionManager().getInteractionCreature());

        if (target == null) {
            client.send(GamePacketFormatter.awayPlayerMessage(Integer.parseInt(data)));
            return;
        }

        gameMap.send(GamePacketFormatter.acceptDuelMessage(client.getPlayer().getId(), target.getId()));

        gameMap.getFightFactory().newDuel(target, client.getPlayer());
    }

    private void joinFight(String data) {
        String[] information = data.split(";");

        if (information.length == 1) {
            int fight = Integer.parseInt(information[0]);

            // TODO: 19/12/2016 join as spectator
        } else {
            Player target = client.getPlayerRepository().get(Integer.parseInt(information[1]));

            if (target != null) {
                Fight fight = target.getFight();
                fight.join(target.getTeam(), client.getPlayer());
            }
        }
    }

}
