package org.graviton.game.creature.npc;

import lombok.Data;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.action.Action;
import org.graviton.xml.XMLElement;

/**
 * Created by Botan on 07/12/2016. 21:19
 */

@Data
public class NpcAnswer {
    private final short id;
    private final Action npcAction;
    private final String data;

    public int TEST; //TODO : REMOVE

    public NpcAnswer(XMLElement element, EntityFactory entityFactory) {
        this.id = element.getAttribute("id").toShort();
        TEST = element.getElementByTagName("type").toShort();
        this.npcAction = entityFactory.getActionRepository().create(element.getElementByTagName("type").toShort());
        this.data = element.getElementByTagName("arguments").toString();
    }

}
