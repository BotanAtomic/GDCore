package org.graviton.game.fight.turn;

import lombok.Data;
import org.graviton.constant.Dofus;
import org.graviton.game.client.player.Player;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

import java.util.concurrent.ScheduledFuture;

/**
 * Created by Botan on 21/12/2016. 21:42
 */

@Data
public class FightTurn {
    private final Fight fight;
    private final Fighter fighter;

    private ScheduledFuture<?> future;

    FightTurn(Fight fight, Fighter fighter) {
        fighter.setTurn(this);
        this.fight = fight;
        this.fighter = fighter;
    }

    private void begin() {
        fighter.send(PlayerPacketFormatter.asMessage((Player) fighter.getCreature()));
        fight.send(FightPacketFormatter.fighterInformationMessage(fight.fighters()));
        fight.send(FightPacketFormatter.turnReadyMessage(fighter.getId()));
        startTurn();
    }

    public void startTurn() {
        fighter.initializeFighterPoints();

        fight.send(FightPacketFormatter.turnStartMessage(fighter.getId(), Dofus.TURN_TIME));
        future = fight.schedule(this::end, Dofus.TURN_TIME + 750);
    }

    public void end() {
        this.future.cancel(true);
        fight.send(FightPacketFormatter.turnEndMessage(this.fighter.getId()));
        fight.getTurnList().next().begin();
    }

    @Override
    public int hashCode() {
        return this.fighter.getId();
    }

}
