package org.graviton.game.interaction.actions;

import org.graviton.game.action.Action;
import org.graviton.game.action.map.MapAction;
import org.graviton.game.interaction.AbstractGameAction;
import org.graviton.game.interaction.Status;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 22/03/2017. 21:12
 */
public class MapGameAction implements AbstractGameAction {
    private Action action;
    private final GameClient gameClient;
    private final String data;

    public MapGameAction(GameClient gameClient, String data) {
        this.gameClient = gameClient;
        this.data = data;
        System.err.println("NEw game map action");
    }

    @Override
    public boolean begin() {
        System.err.println("Begin game map action");
        gameClient.getInteractionManager().setStatus(Status.INTERACTION);
        MapAction action = MapAction.get(Short.parseShort(data.split(";")[1]));
        return (this.action = action.apply(gameClient, gameClient.getPlayer().getMap().getCells().get(Short.parseShort(data.split(";")[0])))) != null;
    }

    @Override
    public void cancel(String data) {
        System.err.println("Cancel game map action");
    }

    @Override
    public void finish(String data) {
        System.err.println("Finish game map action");
        gameClient.getInteractionManager().setStatus(Status.DEFAULT);
        this.action.finish();
        System.err.println("Finish game map action [2]");
    }
}
