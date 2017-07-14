package org.graviton.network.game.protocol;

import org.graviton.game.client.player.Player;
import org.graviton.game.creature.npc.Npc;
import org.graviton.game.creature.npc.NpcTemplate;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.game.quest.Quest;

import java.util.List;

import static org.graviton.utils.Utils.toHex;

/**
 * Created by Botan on 01/12/2016. 14:22
 */
public class NpcPacketFormatter {

    public static String gmMessage(Npc npc, Player player) {
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
        builder.append(';');

        if (template.getExtraClip() > 0 && player != null && player.getQuestByNpc(template.getId()) != null)
            builder.append(';');
        else
            builder.append(template.getExtraClip()).append(';');


        builder.append(template.getCustomArtWork());
        return builder.toString();
    }

    public static String quitMessage() {
        return "DV";
    }

    public static String createDialog(int id) {
        return "DCK" + id;
    }

    public static String questionMessage(String data) {
        return "DQ" + data;
    }

    public static String buyRequestMessage(int npcId) {
        return "ECK0|" + npcId;
    }

    public static String itemListMessage(List<ItemTemplate> itemTemplates) {
        StringBuilder builder = new StringBuilder("EL");
        itemTemplates.forEach(itemTemplate -> builder.append(itemTemplate.parse()).append('|'));
        return builder.substring(0, builder.length() - 1);
    }

}
