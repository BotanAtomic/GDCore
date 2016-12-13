package org.graviton.game.creature.npc;

import lombok.Data;
import org.graviton.game.look.NpcLook;
import org.graviton.xml.XMLElement;


/**
 * Created by Botan on 27/11/16. 23:02
 */
@Data
public class NpcTemplate {
    private final int id;
    private final short size;
    private final byte sex;
    private final String sales, exchanges, initialQuestion;
    private final NpcLook look;


    public NpcTemplate(XMLElement element) {
        this.id = element.getAttribute("id").toInt();
        this.size = element.getElementByTagName("size").toShort();
        this.initialQuestion = element.getElementByTagName("initialQuestion").toString();
        this.sex = element.getElementByTagName("sex").toByte();
        this.sales = element.getElementByTagName("sales").toString();
        this.exchanges = element.getElementByTagName("exchanges").toString();
        this.look = new NpcLook(element);
    }

    public short getInitialQuestion(int gameMap) {
        if (initialQuestion.contains(",")) {
            for (String question : initialQuestion.split("\\|"))
                if (question.split(",")[0].equals(Integer.toString(gameMap)))
                    return Short.parseShort(question.split(",")[1]);
        } else
            return Short.parseShort(initialQuestion);
        return (short) -1;
    }

    public short getSkin() {
        return this.look.getSkin();
    }

    public int[] getColors() {
        return this.look.getColors();
    }

    public String getAccessories() {
        return this.look.getAccessories();
    }

    public byte getExtraClip() {
        return this.look.getExtraClip();
    }

    public short getCustomArtWork() {
        return this.look.getCustomArtWork();
    }
}
