package org.graviton.game.action.npc;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.game.job.JobTemplate;
import org.graviton.network.game.GameClient;

/**
 * Created by Botan on 26/05/17. 21:22
 */

@GameAction(id= 0b110)
public class LearnJob implements Action {

    @Override
    public void apply(GameClient client, Object data) {
        String[] arguments = String.valueOf(data).split(",");

        short gameMap = Short.parseShort(arguments[1]);
        short onSuccess = Short.parseShort(arguments[2]);
        short onFail = Short.parseShort(arguments[3]);


        JobTemplate jobTemplate = client.getEntityFactory().getJobTemplate(Short.parseShort(arguments[0]));
        
        if(jobTemplate.isBasic()) {
            if(client.getPlayer().getJobs().get(jobTemplate.getId()) != null) {
                if(client.getPlayer().getJobs().values().stream().filter(job -> job.getLevel() < 30 && job.getJobTemplate().isBasic()).count() == 0) {

                } else {

                }
            }
        }

    }

    @Override
    public void finish() {

    }
}
