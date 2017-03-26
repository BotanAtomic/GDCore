package org.graviton.game.maps.object;

import lombok.Data;
import org.graviton.game.maps.GameMap;
import org.graviton.xml.XMLElement;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Botan on 22/03/2017. 18:31
 */

@Data
public class InteractiveObjectTemplate {
    private final int id;
    private final String name;
    private final long respawnTime;
    private final int duration;

    public InteractiveObjectTemplate(XMLElement element) {
        this.id = element.getAttribute("id").toInt();
        this.name = element.getAttribute("name").toString();
        this.respawnTime = element.getElementByTagName("time").toLong();
        this.duration = element.getElementByTagName("duration").toInt();
    }

    public InteractiveObject newInteractiveObject(GameMap gameMap, short cell, ScheduledExecutorService scheduledExecutorService) {
        return new InteractiveObject(this, gameMap, cell, scheduledExecutorService);
    }
}
