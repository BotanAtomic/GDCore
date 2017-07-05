package org.graviton.game.interaction;

import javafx.util.Pair;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.graviton.game.hdv.SellPoint;
import org.graviton.game.house.House;
import org.graviton.game.interaction.actions.*;
import org.graviton.game.job.action.JobAction;
import org.graviton.game.maps.object.InteractiveObject;
import org.graviton.game.trunk.type.Trunk;
import org.graviton.network.game.GameClient;

import java.util.ArrayDeque;


/**
 * Created by Botan on 16/11/2016 : 21:03
 */
@Data
@Slf4j
public class InteractionManager extends ArrayDeque<AbstractGameAction> {
    private final GameClient client;

    private int interactionCreature;

    private House houseInteraction;

    private Trunk trunkInteraction;

    private SellPoint sellPointInteraction;

    private Pair<InteractiveObject,JobAction> currentJobAction;

    private Status status = Status.DEFAULT;

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
                new SpellAttack(client.getPlayer(), new short[]{Short.parseShort(data.split(";")[0]), Short.parseShort(data.split(";")[1])}).begin();
                break;

            case WEAPON_ATTACK:
                new WeaponAttack(client.getPlayer(), Short.parseShort(data)).begin();
                break;

            case ASK_DEFY:
                new AskDefy(client, Integer.parseInt(data)).begin();
                break;

            case ACCEPT_DEFY:
                new AcceptDefy(client, interactionCreature, client.getPlayer().getGameMap()).begin();
                break;

            case CANCEL_DEFY:
                new CancelDefy(client, interactionCreature).begin();
                break;

            case JOIN_FIGHT:
                new JoinFight(client, data.split(";")).begin();
                break;

            case AGGRESSION:
                new Aggression(client.getPlayer(), client.getPlayerRepository().find(Integer.parseInt(data))).begin();
                break;

            case MAP_ACTION:
                addAction(new GameMapAction(client, data));
                break;

            case HOUSE_ACTION:
                new HouseAction(client, Short.parseShort(data));
                break;

            default:
                log.error("not implemented game action : {}", id);
        }
    }


    public void end(AbstractGameAction gameAction, boolean success, String data) {
        if (gameAction != null) {
            if (success) gameAction.finish(data);
            else gameAction.cancel(data);
        }

        AbstractGameAction abstractGameAction = super.peekFirst();
        if (abstractGameAction != null)
            abstractGameAction.begin();
    }

    private void addAction(AbstractGameAction gameAction) {
        super.add(gameAction);

        if (super.size() == 1)
            if (!gameAction.begin())
                super.pollFirst();


    }

    public void setInteractionWith(int creature) {
        this.interactionCreature = creature;
    }

    public int getInteractionCreature() {
        return this.interactionCreature;
    }

    public boolean isBusy() {
        return this.status != Status.DEFAULT;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
