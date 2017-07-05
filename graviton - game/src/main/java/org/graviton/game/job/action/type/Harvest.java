package org.graviton.game.job.action.type;

import javafx.util.Pair;
import org.graviton.constant.Dofus;
import org.graviton.game.client.player.Player;
import org.graviton.game.interaction.Status;
import org.graviton.game.items.Item;
import org.graviton.game.job.Job;
import org.graviton.game.job.action.JobAction;
import org.graviton.game.maps.object.InteractiveObject;
import org.graviton.game.maps.object.InteractiveObjectState;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.ItemPacketFormatter;
import org.graviton.utils.Cells;

import java.security.SecureRandom;


/**
 * Created by Botan on 03/07/17. 16:53
 */
public class Harvest extends JobAction {
    private short time;
    protected byte[] differentialGains;

    public Harvest(short id, byte level, byte minimum, byte gain, Job job) {
        super(id, gain, job);
        this.time = (short) (12000 - (level * 100));
        this.differentialGains = new byte[]{(byte) (minimum + level / 5), (byte) ((minimum + 1) + (level / 5))};
    }

    @Override
    public String toString() {
        return "," + super.getId() + "~" + this.differentialGains[0] + "~" + this.differentialGains[1] + "~0~" + this.time;
    }

    @Override
    public void start(Player player, short gameAction, InteractiveObject interactiveObject) {
        if (interactiveObject.getState() == InteractiveObjectState.EMPTY || Cells.distanceBetween(player.getMap().getWidth(), player.getCell().getId(), interactiveObject.getCell()) > 2) {
            player.send(GamePacketFormatter.noActionMessage());
            player.getAccount().getClient().getInteractionManager().clear();
            return;
        }

        player.setStatus(Status.WALKING);
        player.getAccount().getClient().getInteractionManager().setCurrentJobAction(new Pair<>(interactiveObject, this));
        interactiveObject.setInteractive(false);
        interactiveObject.setState(InteractiveObjectState.EMPTYING);
        player.getMap().send(interactiveObject.getData());
        player.getGameMap().send(GamePacketFormatter.harvestActionMessage(gameAction, player.getId(), interactiveObject.getCell(), time));
    }

    @Override
    public void stop(Player player, InteractiveObject interactiveObject) {
        player.getAccount().getClient().getInteractionManager().setCurrentJobAction(null);
        interactiveObject.setState(InteractiveObjectState.EMPTY);
        player.getMap().send(interactiveObject.getData());

        job.addExperience(player, gain);

        byte quantity = (byte) (new SecureRandom().nextInt(differentialGains[1] - differentialGains[0] + 1) + differentialGains[0]);
        Item item = player.getEntityFactory().getItemTemplate(Dofus.getObjectByAction(this.id)).createMax(player.getEntityFactory().getNextItemId());

        if (player.getInventory().addItem(item, true) == null)
            player.send(ItemPacketFormatter.addItemMessage(item));

        player.send(GamePacketFormatter.quantityAnimationMessage(player.getId(), quantity));

        //TODO : protector
    }
}
