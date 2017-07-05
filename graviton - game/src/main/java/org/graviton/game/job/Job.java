package org.graviton.game.job;

import lombok.Data;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.client.player.Player;
import org.graviton.game.experience.Experience;
import org.graviton.game.job.action.JobAction;
import org.graviton.game.maps.object.InteractiveObject;
import org.graviton.network.game.protocol.JobPacketFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Botan on 30/06/17. 17:32
 */

@Data
public class Job {
    private final JobTemplate jobTemplate;
    private final EntityFactory entityFactory;
    private byte level;
    private long experience;

    private List<JobAction> actions;

    public Job(JobTemplate jobTemplate, long experience, EntityFactory entityFactory) {
        this.jobTemplate = jobTemplate;
        this.experience = experience;
        this.level = entityFactory.getJobLevel(experience);
        this.entityFactory = entityFactory;
        this.actions = jobTemplate.getActionGetter() != null ? jobTemplate.getActionGetter().get(this) : new ArrayList<>();
    }

    public void addExperience(Player player,long experience) {
        this.experience += experience;

        while (this.experience >= entityFactory.getExperience((short) (level + 1)).getJob() && level < 100)
            levelUp(player);

        player.send(JobPacketFormatter.statisticsJobMessage(this));
    }

    private void levelUp(Player player) {
        this.level++;
        player.send(JobPacketFormatter.jobLevelMessage(jobTemplate.getId(), this.level));
        this.actions = jobTemplate.getActionGetter() != null ? jobTemplate.getActionGetter().get(this) : new ArrayList<>();
        player.send(JobPacketFormatter.startJobMessage(this));
    }

    public String compileExperience() {
        Experience experience = this.entityFactory.getExperience(this.level);
        return experience.getJob() + ";" + this.experience + ";" + (this.level < 100 ? experience.getNext().getJob() : experience.getJob());
    }

    public String statisticsPacket() {
        StringBuilder builder = new StringBuilder();
        this.actions.forEach(builder::append);
        return "|".concat(String.valueOf(this.jobTemplate.getId()).concat(";")).concat(builder.length() > 2 ? builder.toString().substring(1) : builder.toString());
    }

    public void startAction(Player player, short actionId, InteractiveObject interactiveObject) {
        JobAction jobAction = getJobAction(actionId);
        jobAction.start(player, actionId, interactiveObject);
    }

    private JobAction getJobAction(short actionId) {
        return actions.stream().filter(action -> action.getId() == actionId).findAny().orElse(null);
    }

}
