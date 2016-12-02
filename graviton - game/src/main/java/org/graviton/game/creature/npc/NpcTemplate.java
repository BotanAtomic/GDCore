package org.graviton.game.creature.npc;

import lombok.Data;
import org.graviton.game.look.NpcLook;
import org.w3c.dom.Element;


/**
 * Created by Botan on 27/11/16.
 */
@Data
public class NpcTemplate {
    private final int id;
    private final short size;
    private final byte sex;
    private final String sales, exchanges, initialQuestion;
    private final NpcLook look;

    private final Element element;

    public NpcTemplate(Element element) {
        this.element = element;
        this.id = Integer.parseInt(element.getAttribute("id"));
        this.size = Short.parseShort(getTag("size"));
        this.initialQuestion = getTag("initialQuestion");
        this.sex = Byte.parseByte(getTag("sex"));
        this.sales = getTag("sales");
        this.exchanges = getTag("exchanges");
        this.look = new NpcLook(this);
    }

    public String getTag(String tag) {
        return this.element.getElementsByTagName(tag).item(0).getTextContent();
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
