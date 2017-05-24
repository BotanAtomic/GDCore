package org.graviton.game.interaction.actions;


import org.graviton.game.client.player.Player;
import org.graviton.game.creature.monster.MonsterGroup;
import org.graviton.game.interaction.AbstractGameAction;
import org.graviton.game.interaction.Status;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.cell.Trigger;
import org.graviton.game.paths.Path;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.utils.Cells;

import java.util.Optional;


/**
 * Created by Botan on 16/11/2016 : 21:05
 */
public class PlayerMovement extends Path implements AbstractGameAction {
    private final GameMap gameMap;
    private final Player player;

    private Cell newCell;

    public PlayerMovement(Player player, String path) {
        super(path, player.getGameMap(), player.getCell().getId(), player);
        this.player = player;
        this.gameMap = player.getGameMap();
    }

    @Override
    public boolean begin() {
        if (player.getPods()[0] >= player.getPods()[1]) {
            player.send(MessageFormatter.maxPodsReached());
            return false;
        }

        boolean valid = super.isValid();

        gameMap.send(valid ? GamePacketFormatter.creatureMovementMessage((short) 1, player.getId(), super.toString()) : GamePacketFormatter.noActionMessage());

        if (!valid)
            return false;

        player.setStatus(Status.WALKING);

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
        player.setStatus(Status.DEFAULT);

        player.getLocation().setOrientation(getOrientation());

        if(tasks.isEmpty()) {
            Optional<MonsterGroup> groupOptional = gameMap.monsters().stream()
                    .filter(creature -> Cells.distanceBetween(gameMap.getWidth(), creature.getLocation().getCell().getId(), newCell.getId()) < 2).findFirst();

            if (groupOptional.isPresent()) {

                byte alignment = groupOptional.get().alignment();
                if(alignment != 0 && player.getAlignment().getId() == alignment)
                    return;

                gameMap.getFightFactory().newMonsterFight(player, groupOptional.get());
                return;
            }

            player.getLocation().setCell(newCell);
            Trigger trigger = gameMap.getTriggers().get(newCell.getId());

            if (trigger != null)
                player.changeMap(trigger.getNextMap(), trigger.getNextCell());
        } else
            tasks.forEach(Runnable::run);
    }

}
