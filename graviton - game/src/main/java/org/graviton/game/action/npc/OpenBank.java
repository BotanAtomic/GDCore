package org.graviton.game.action.npc;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.game.client.player.Player;
import org.graviton.game.exchange.type.TrunkExchange;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.ExchangePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

/**
 * Created by Botan on 13/05/17. 17:55
 */

@GameAction(id = -1)
public class OpenBank implements Action {

    @Override
    public void apply(GameClient client, Object data) {
        Player player = client.getPlayer();

        if (player.getAlignment().getDishonor() > 0)
            player.send(MessageFormatter.notPermittedDishonorMessage());
        else {
            int bankCost = player.getAccount().getBank().getCost();

            if (player.getInventory().getKamas() >= bankCost) {
                player.getInventory().addKamas(bankCost * -1);
                player.send(PlayerPacketFormatter.asMessage(player));
                player.send(MessageFormatter.bankAccessMessage(bankCost));

                player.send(ExchangePacketFormatter.startMessage((byte) 5));
                player.send(ExchangePacketFormatter.trunkMessage(player.getAccount().getBank()));
                player.setExchange(new TrunkExchange(player, player.getAccount().getBank()));
            } else
                player.send(MessageFormatter.needMorForTrunkMessage((int) (bankCost - player.getInventory().getKamas())));


        }
    }

    @Override
    public void finish() {

    }
}
