package org.graviton.game.action.npc;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.game.client.player.Player;
import org.graviton.game.items.Item;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.ItemPacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;

import static org.graviton.lang.LanguageSentence.NEED_DUNGEON_KEY;

/**
 * Created by Botan on 26/03/2017. 13:08
 */

@GameAction(id=15)
public class EnterDungeon implements Action {
    @Override
    public void apply(GameClient client, Object data) {
        Player player = client.getPlayer();

        String argument[] = ((String) data).split(",");

        if (player.getMap().getId() == Integer.parseInt(argument[3])) {
            Item item = player.getInventory().haveItem(Integer.parseInt(argument[2]));
            if (item != null) {
                player.changeMap(Integer.parseInt(argument[0]), Short.parseShort(argument[1]));
                if (item.getQuantity() - 1 <= 0) {
                    player.removeItem(item);
                    player.send(ItemPacketFormatter.deleteMessage(item.getId()));
                } else {
                    item.changeQuantity((short) -1);
                    player.send(ItemPacketFormatter.quantityMessage(item.getId(), item.getQuantity()));
                }

            } else
                player.send(MessageFormatter.customStaticMessage(client.getAccount().getClient().getLanguage().getSentence(NEED_DUNGEON_KEY)));
        }


    }

    @Override
    public void finish() {

    }
}
