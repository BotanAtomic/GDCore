package org.graviton.game.job.action.type;

import javafx.util.Pair;
import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.game.exchange.type.BreakerExchange;
import org.graviton.game.interaction.Status;
import org.graviton.game.job.Job;
import org.graviton.game.job.action.JobAction;
import org.graviton.game.job.craft.CraftData;
import org.graviton.game.maps.object.InteractiveObject;
import org.graviton.game.maps.object.InteractiveObjectState;
import org.graviton.network.game.protocol.ExchangePacketFormatter;
import org.graviton.network.game.protocol.GamePacketFormatter;

import java.util.Map;

/**
 * Created by Botan on 03/07/17. 17:25
*/
@Data
public class Craft extends JobAction {
    private byte chance;
    private byte maxCase;

    private InteractiveObject interactiveObject;

    public Craft(short id, byte gain, byte chance, byte maxCase, Job job) {
        super(id, gain, job);
        this.maxCase = maxCase;
        this.chance = chance;
    }

    @Override
    public String toString() {
        return "," + super.getId() + "~" + this.maxCase + "~0~0~" + this.chance;
    }

    @Override
    public void start(Player player, short gameAction, InteractiveObject interactiveObject) {
        this.interactiveObject = interactiveObject;
        interactiveObject.setState(InteractiveObjectState.EMPTYING);
        player.setStatus(Status.EXCHANGE);
        player.getAccount().getClient().getInteractionManager().setCurrentJobAction(new Pair<>(interactiveObject, this));
        player.send(ExchangePacketFormatter.startMessage((byte) 3, this.maxCase + ";" + this.id));
        player.getMap().send(interactiveObject.getData());
        player.setExchange(new BreakerExchange(player, this, interactiveObject.getScheduledExecutorService()));
    }

    @Override
    public void stop(Player player, InteractiveObject interactiveObject) {
        player.getAccount().getClient().getInteractionManager().setCurrentJobAction(null);
        player.send(ExchangePacketFormatter.cancelMessage());
        player.setStatus(Status.DEFAULT);
        this.interactiveObject.setState(InteractiveObjectState.FULL);
        player.getMap().send(this.interactiveObject.getData());
        player.setExchange(null);
        player.getAccount().getClient().getInteractionManager().clear();
        player.send(GamePacketFormatter.noActionMessage());
    }

    public CraftData get(Map<Short, Short> ingredients) {
        return this.getJob().getJobTemplate().getCrafts(id, maxCase).stream().filter(data -> data.check(ingredients)).findAny().orElse(null);
    }
}
