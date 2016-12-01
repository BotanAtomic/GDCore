package org.graviton.game.interaction.actions;


import org.graviton.game.client.player.Player;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.cell.Trigger;
import org.graviton.game.paths.Path;
import org.graviton.network.game.protocol.GameProtocol;
import org.graviton.network.game.protocol.MessageProtocol;


/**
 * Created by Botan on 16/11/2016 : 21:05
 */
public class PlayerMovement extends Path implements AbstractGameAction {
    private final GameMap gameMap;
    private final Player player;

    private Cell newCell;

    public PlayerMovement(Player player, String path) {
        super(path, player.getGameMap(), player.getCell().getId());
        this.player = player;
        this.gameMap = player.getGameMap();
    }

    @Override
    public boolean begin() {
        if (player.getPods()[0] >= player.getPods()[1]) {
            player.send(MessageProtocol.maxPodsReached());
            return false;
        }

        gameMap.send(GameProtocol.creatureMovementMessage((short) 1, player.getId(), super.toString()));

        if (!isValid())
            return false;

        initialize();

        newCell = gameMap.getCells().get(getCell());
        return true;
    }

    @Override
    public void cancel(String data) {
        player.getLocation().setCell(gameMap.getCells().get(Short.parseShort(data.substring(2))));
        player.getLocation().setOrientation(getOrientation());
    }

    @Override
    public void finish(String data) {
        player.getLocation().setOrientation(getOrientation());

        Trigger trigger = gameMap.getTriggers().get(newCell.getId());

        if (trigger != null)
            player.changeMap(trigger.getNextMap(), trigger.getNextCell());
        else
            player.getLocation().setCell(newCell);

    }

}
