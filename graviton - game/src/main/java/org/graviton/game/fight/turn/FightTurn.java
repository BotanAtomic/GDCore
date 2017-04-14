package org.graviton.game.fight.turn;

import lombok.Data;
import org.graviton.constant.Dofus;
import org.graviton.game.client.player.Player;
import org.graviton.game.effect.buff.Buff;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightState;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Botan on 21/12/2016. 21:42
 */

@Data
public class FightTurn {
    private final Fight fight;
    private final Fighter fighter;

    private Timer timer;
    private TimerTask timerTask;

    public FightTurn(Fight fight, Fighter fighter) {
        fighter.setTurn(this);
        this.fight = fight;
        this.fighter = fighter;
    }

    public void begin() {
        if (fighter.isDead() || fighter.isPassTurn()) {
            fighter.setPassTurn(false);
            end(true);
            return;
        }

        if (fighter.getCreature() instanceof Player)
            fighter.send(PlayerPacketFormatter.asMessage((Player) fighter.getCreature()));

        fight.send(FightPacketFormatter.fighterInformationMessage(fight.fighters()));
        fight.send(FightPacketFormatter.turnReadyMessage(fighter.getId()));
        fight.send(FightPacketFormatter.turnStartMessage(fighter.getId(), Dofus.TURN_TIME));
        startTurn();
    }

    private void startTurn() {
        fight.checkGlyph(fighter);
        fighter.getBuffs().forEach(Buff::check);
        fighter.getSpellCounter().decrementSpellTime();

        if (fighter.artificialIntelligence() != null)
            fighter.artificialIntelligence().start();

        fighter.setOnAction(false);
        scheduleTurn();
    }

    private void scheduleTurn() {
        (timer = new Timer()).schedule(this.timerTask = new TimerTask() {
            @Override
            public void run() {
                end(true);
            }
        }, Dofus.TURN_TIME + 750);
    }

    public void end(boolean forced) {
        if (fight.getState() != FightState.FINISHED && fight.getTurnList().getCurrent().fighter.getId() == this.fighter.getId()) {
            long time = System.currentTimeMillis();

            if (!forced)
                while (fighter.isOnAction() && (System.currentTimeMillis() - time) < 1100) ;
            fight.send(FightPacketFormatter.turnEndMessage(this.fighter.getId()));
            fighter.setOnAction(false);
            new ArrayList<>(fighter.getBuffs()).forEach(Buff::decrement);
            fighter.initializeFighterPoints();
            fighter.getSpellCounter().resetPerTurn();
            fight.getTurnList().next().begin();

            if (timer != null)
                timer.cancel();

            if (timerTask != null)
                timerTask.cancel();
        }
    }

    @Override
    public int hashCode() {
        return this.fighter.getId();
    }

}
