package org.graviton.game.action.npc;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.SpellPacketFormatter;

/**
 * Created by Botan on 22/04/17. 03:33
 */

@GameAction(id=14)
public class ForgetSpell implements Action {

    @Override public void apply(GameClient client, Object data) {
        client.send(SpellPacketFormatter.openForgetSpellWindowsMessage());
    }

    @Override public void finish() {

    }

}
