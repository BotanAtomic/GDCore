package org.graviton.game.job.action;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.game.job.Job;
import org.graviton.game.maps.object.InteractiveObject;

/**
 * Created by Botan on 03/07/17. 16:51
 */
@Data
public abstract class JobAction {
    protected short id;
    protected byte gain;
    protected Job job;

    public JobAction(short id, byte gain, Job job) {
        this.id = id;
        this.gain = gain;
        this.job = job;
    }

    public abstract String toString();

    public abstract void start(Player player, short gameAction, InteractiveObject interactiveObject);

    public abstract void stop(Player player, InteractiveObject interactiveObject);

}
