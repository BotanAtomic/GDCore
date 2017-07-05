package org.graviton.game.interaction.actions;

import javafx.util.Pair;
import org.graviton.constant.Dofus;
import org.graviton.game.action.Action;
import org.graviton.game.client.player.Player;
import org.graviton.game.interaction.AbstractGameAction;
import org.graviton.game.interaction.Status;
import org.graviton.game.job.Job;
import org.graviton.game.job.action.JobAction;
import org.graviton.game.maps.object.InteractiveObject;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 22/03/2017. 21:12
 */
public class GameMapAction implements AbstractGameAction {
    private Action action;
    private final GameClient gameClient;
    private final String data;

    public GameMapAction(GameClient gameClient, String data) {
        this.gameClient = gameClient;
        this.data = data;
    }

    @Override
    public boolean begin() {
        gameClient.getInteractionManager().setStatus(Status.INTERACTION);

        short actionId = Short.parseShort(data.split(";")[1]);

        if (Dofus.isJobAction(actionId)) {
            if(gameClient.getInteractionManager().getCurrentJobAction() != null)
                return false;

            Player player = gameClient.getPlayer();
            short cell = Short.parseShort(data.split(";")[0]);
            player.getJobs().get(gameClient.getEntityFactory().getJobIdByAction(player, actionId)).startAction(player, actionId, player.getGameMap().getCell(cell).getInteractiveObject());
            return true;
        }

        this.action = gameClient.getEntityFactory().getActionRepository().create(actionId);

        if (this.action != null) {
            this.action.apply(gameClient, gameClient.getPlayer().getMap().getCell(Short.parseShort(data.split(";")[0])));
            return true;
        }

        return false;
    }

    @Override
    public void cancel(String data) {
        System.err.println("Cancel game map action");
    }

    @Override
    public void finish(String data) {
        gameClient.getInteractionManager().setStatus(Status.DEFAULT);
        if (this.action != null)
            this.action.finish();
        else {
            Pair<InteractiveObject, JobAction> jobData = gameClient.getInteractionManager().getCurrentJobAction();
            jobData.getValue().stop(gameClient.getPlayer(), jobData.getKey());
        }
    }
}
