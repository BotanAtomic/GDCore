package org.graviton.game.action.npc;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.type.PlayerStatistics;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

/**
 * Created by Botan on 23/04/17. 01:06
 */

@GameAction(id=13)
public class ResetStatistics implements Action {

    @Override public void apply(GameClient client, Object data) {
        PlayerStatistics statistics = client.getPlayer().getStatistics();
        short lastPoints = statistics.getStatisticPoints();
        CharacteristicType.base.forEach(characteristicType -> statistics.get(characteristicType).resetBase());
        statistics.setStatisticPoints((short) ((client.getPlayer().getLevel() - 1) * 5));

        client.send(PlayerPacketFormatter.asMessage(client.getPlayer()));
        client.send(MessageFormatter.regainStatisticPointsMessage((short) (statistics.getStatisticPoints() - lastPoints)));
        client.getPlayer().update();

    }

    @Override public void finish() {

    }
}
