package org.graviton.game.creature.npc;

import lombok.Data;
import org.graviton.game.action.npc.NpcAction;
import org.graviton.xml.XMLElement;

/**
 * Created by Botan on 07/12/2016. 21:19
 */

@Data
public class NpcAnswer {
    private final short id;
    private final NpcAction npcAction;
    private final String data;

    public NpcAnswer(XMLElement element) {
        this.id = element.getAttribute("id").toShort();
        this.npcAction = NpcAction.get(element.getElementByTagName("type").toShort());
        this.data = element.getElementByTagName("arguments").toString();
    }

}
