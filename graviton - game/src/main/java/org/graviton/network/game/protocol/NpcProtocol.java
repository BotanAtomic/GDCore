package org.graviton.network.game.protocol;

import org.graviton.game.creature.npc.Npc;
import org.graviton.game.creature.npc.NpcTemplate;

import static org.graviton.utils.StringUtils.toHex;

/**
 * Created by Botan on 01/12/2016.
 */
public class NpcProtocol {

    public static String gmMessage(Npc npc) {
        NpcTemplate template = npc.getTemplate();

        StringBuilder builder = new StringBuilder();
        builder.append(npc.getLocation().getCell().getId()).append(';');
        builder.append(npc.getLocation().getOrientation().ordinal()).append(';');
        builder.append("0;");
        builder.append(npc.getId()).append(';');

        builder.append(npc.getTemplate().getId()).append(';');
        builder.append("-4;"); // npc type = -4
        builder.append(template.getSkin()).append('^');
        builder.append(template.getSize()).append(';');
        builder.append(template.getSex()).append(';');
        builder.append(toHex(template.getColors()[0])).append(';');
        builder.append(toHex(template.getColors()[1])).append(';');
        builder.append(toHex(template.getColors()[2])).append(';');
        builder.append(template.getAccessories());
        builder.append(',');
        builder.append(template.getExtraClip()).append(';');
        builder.append(template.getCustomArtWork());
        return builder.toString();
    }

}
