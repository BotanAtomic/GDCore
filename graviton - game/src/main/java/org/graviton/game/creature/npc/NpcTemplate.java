package org.graviton.game.creature.npc;

import lombok.Data;
import org.graviton.utils.StringUtils;
import org.w3c.dom.Element;


/**
 * Created by kurdistan on 27/11/16.
 */
@Data
public class NpcTemplate {
    private final int id;
    private final short skin, size, customArtWork;
    private final byte sex, extraClip;
    private final int[] colors;
    private final String accessories, sales, exchanges, initialQuestion;

    private final Element element;

    public NpcTemplate(Element element) {
        this.element = element;
        this.id = Integer.parseInt(element.getAttribute("id"));
        this.skin = Short.parseShort(getTag("skin"));
        this.size = Short.parseShort(getTag("size"));
        this.initialQuestion = getTag("initialQuestion");
        this.sex = Byte.parseByte(getTag("sex"));
        this.customArtWork = Short.parseShort(getTag("customArtWork"));
        this.extraClip = Byte.parseByte(getTag("extraClip"));
        this.colors = StringUtils.parseColors(getTag("colors"), ",");
        this.accessories = getTag("accessories");
        this.sales = getTag("sales");
        this.exchanges = getTag("exchanges");
    }

    private String getTag(String tag) {
        return this.element.getElementsByTagName(tag).item(0).getTextContent();
    }

}
