package org.graviton.game.action.map;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.game.alignment.type.AlignmentType;
import org.graviton.game.client.player.Player;
import org.graviton.game.zaap.Zaapi;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.PlayerPacketFormatter;


/**
 * Created by Botan on 13/05/17. 11:29
 */

@GameAction(id = 157)
public class OpenZaapiAction implements Action {

    @Override
    public void apply(GameClient client, Object data) {
        Player player = client.getPlayer();
        Zaapi zaapi = player.getGameMap().getZaapi();

        if(AlignmentType.isOpposit(player.getAlignment().getType(), zaapi.getAlignment())) {
            player.send(PlayerPacketFormatter.zaapiListMessage(null, (byte) 20, player.getMap().getId()));
        } else player.send(PlayerPacketFormatter.zaapiListMessage(player.getEntityFactory().getZaapis(zaapi), ((byte) (zaapi.getAlignment().equals(player.getAlignment()) ? 10 : 20)), player.getMap().getId()));



    }

    @Override
    public void finish() {

    }
}
