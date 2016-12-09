package org.graviton.game.look;

import org.graviton.utils.Utils;
import org.graviton.xml.XMLElement;

/**
 * Created by Botan on 02/12/2016.
 */
public class NpcLook extends AbstractLook {
    private final String accessories;
    private final byte extraClip;
    private final short customArtWork;

    public NpcLook(XMLElement element) {
        super(Utils.parseColors(element.getElementByTagName("colors").toString(), ","), element.getElementByTagName("skin").toShort());
        this.accessories = element.getElementByTagName("accessories").toString();
        this.customArtWork = element.getElementByTagName("customArtWork").toShort();
        this.extraClip = element.getElementByTagName("extraClip").toByte();
    }

    public short getSkin() {
        return super.getSkin();
    }

    public int[] getColors() {
        return super.getColors();
    }

    public String getAccessories() {
        return this.accessories;
    }

    public byte getExtraClip() {
        return this.extraClip;
    }

    public short getCustomArtWork() {
        return this.customArtWork;
    }
}


