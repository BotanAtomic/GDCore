package org.graviton.game.look;

import lombok.Data;
import org.graviton.game.creature.npc.NpcTemplate;
import org.graviton.utils.StringUtils;

/**
 * Created by Botan on 02/12/2016.
 */
@Data
public class NpcLook extends AbstractLook {
    private final String accessories;

    private final byte extraClip;
    private final short customArtWork;

    public NpcLook(NpcTemplate template) {
        super(StringUtils.parseColors(template.getTag("colors"), ","), Short.parseShort(template.getTag("skin")));
        this.accessories = template.getTag("accessories");
        this.customArtWork = Short.parseShort(template.getTag("customArtWork"));
        this.extraClip = Byte.parseByte(template.getTag("extraClip"));
    }

}
