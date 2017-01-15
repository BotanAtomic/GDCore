package org.graviton.game.fight.turn;

import lombok.Data;
import org.graviton.constant.Dofus;
import org.graviton.game.client.player.Player;
import org.graviton.game.effect.buff.Buff;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

import java.util.ArrayList;
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

    public void begin() {
        fighter.send(PlayerPacketFormatter.asMessage((Player) fighter.getCreature()));
        fight.send(FightPacketFormatter.fighterInformationMessage(fight.fighters()));
        fight.send(FightPacketFormatter.turnReadyMessage(fighter.getId()));
        fight.send(FightPacketFormatter.turnStartMessage(fighter.getId(), Dofus.TURN_TIME));

        if (fighter.isPassTurn()) {
            fighter.setPassTurn(false);
            end();
            return;
        }

        startTurn();
    }

    private void startTurn() {
        fight.checkGlyph(fighter);
        fighter.getBuffs().forEach(Buff::check);
        future = fight.schedule(this::end, Dofus.TURN_TIME + 750);
    }

    public void end() {
        fight.schedule(() -> {
            if (future != null)
                this.future.cancel(true);
            fight.send(FightPacketFormatter.turnEndMessage(this.fighter.getId()));
            fight.getTurnList().next().begin();
            fighter.setOnAction(false);
            new ArrayList<>(fighter.getBuffs()).forEach(Buff::decrement);
            fighter.initializeFighterPoints();
            fighter.clearLaunchedSpell();
        }, fighter.isOnAction() ? 1100 : 1);
    }

    @Override
    public int hashCode() {
        return this.fighter.getId();
    }

}
