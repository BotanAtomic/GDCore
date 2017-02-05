package org.graviton.game.interaction.actions;


import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.interaction.AbstractGameAction;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.fight.FightMap;
import org.graviton.game.paths.Path;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.utils.Utils;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by Botan on 16/11/2016 : 21:05
 */
public class FightMovement extends Path implements AbstractGameAction {
    private final FightMap fightMap;
    private final Fighter fighter;

    private Cell newCell;

    public FightMovement(Fighter fighter, String path) {
        super(path, fighter.getLocation().getMap(), fighter.getFightCell().getId(), fighter);
        this.fighter = fighter;
        this.fightMap = (FightMap) fighter.getLocation().getMap();
        fighter.setOnAction(true);
    }

    /**
     * Formulas
     **/

    private static short getTackleChance(Fighter fighter, Collection<Fighter> enemies) {
        int agility = fighter.getStatistics().get(CharacteristicType.Agility).total();
        AtomicInteger enemiesAgility = new AtomicInteger(0);
        AtomicBoolean blocked = new AtomicBoolean(fighter.isStatic());
        enemies.forEach(enemy -> {
            enemiesAgility.addAndGet(enemy.getStatistics().get(CharacteristicType.Agility).total());
            if (enemy.isStatic() && !blocked.get())
                blocked.set(true);
        });
        return blocked.get() ? 0 : (short) ((300 * (agility + 25) / ((short) (agility + enemiesAgility.get() + (50 * enemies.size())))) - 100);
    }

    @Override
    public boolean begin() {
        boolean valid = super.isValid() && fighter.getCurrentMovementPoint() >= super.size();

        Collection<Fighter> aroundFighters = getAroundFighters(fightMap, fighter, fighter.getFightCell().getId());

        if (!aroundFighters.isEmpty()) {
            short tackleChance = getTackleChance(fighter, aroundFighters);

            if (Utils.random(0, 100) > tackleChance) {
                fightMap.send(FightPacketFormatter.actionMessage(FightAction.TACKLE, fighter.getId()));

                short looseActionPoint = (short) (fighter.getCurrentActionPoint() * tackleChance / 100);

                if (fighter.getCurrentActionPoint() < looseActionPoint)
                    looseActionPoint = fighter.getCurrentActionPoint();

                fightMap.send(FightPacketFormatter.movementPointEventMessage(fighter.getId(), (byte) (fighter.getCurrentMovementPoint() * -1)));
                fightMap.send(FightPacketFormatter.actionPointEventMessage(fighter.getId(), (byte) ((fighter.getCurrentActionPoint() - looseActionPoint) * -1)));

                fighter.setCurrentMovementPoint((byte) 0);
                fighter.setCurrentActionPoint((byte) (fighter.getCurrentActionPoint() - looseActionPoint));

                return false;
            }

        }

        fightMap.send(valid ? GamePacketFormatter.creatureMovementMessage((short) 1, fighter.getId(), super.toString()) : GamePacketFormatter.noActionMessage());

        if (!valid)
            return false;

        initialize();

        newCell = fightMap.getCells().get(getCell());
        return true;
    }

    @Override
    public void cancel(String data) {

    }

    @Override
    public void finish(String data) {
        finish(fighter);

        if (fighter.getHolding() != null)
            finish(fighter.getHolding());

        fightMap.send(FightPacketFormatter.movementPointEventMessage(fighter.getId(), (byte) (super.size() * -1)));
        fighter.setCurrentMovementPoint((byte) (fighter.getCurrentMovementPoint() - super.size()));
        if (!tasks.isEmpty())
            tasks.forEach(Runnable::run);

        fighter.setOnAction(false);
    }

    private void finish(Fighter fighter) {
        fighter.getLocation().getCell().getCreatures().remove(fighter.getId());
        newCell.getCreatures().add(fighter.getId());

        fighter.getLocation().setOrientation(getOrientation());
        fighter.getLocation().setCell(newCell);
    }

}
