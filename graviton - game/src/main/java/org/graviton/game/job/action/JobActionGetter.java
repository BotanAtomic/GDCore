package org.graviton.game.job.action;

import org.graviton.game.job.Job;

import java.util.List;

/**
 * Created by Botan on 03/07/17. 17:14
 */
@FunctionalInterface
public interface JobActionGetter {

    static byte getChance(byte level) {
        return (byte) (level < 10 ? 50 : 54 + (level / 10 - 1) * 5);
    }

    static byte getMaxCase(byte level) {
        return level < 10 ? 2 : level == 100 ? 9 : (byte) (level / 20 + 3);
    }

    List<JobAction> get(Job job);

}
