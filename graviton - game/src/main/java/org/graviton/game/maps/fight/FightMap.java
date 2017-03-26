package org.graviton.game.maps.fight;

import lombok.Data;
import org.graviton.api.Creature;
import org.graviton.game.fight.Fight;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.utils.CellLoader;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.network.game.protocol.GamePacketFormatter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Botan on 14/12/2016. 16:06
 */

@Data
public class FightMap implements AbstractMap {
    private final GameMap model;

    private final int id;
    private final Map<Short, Cell> cells;
    private final List<Creature> fighters = new CopyOnWriteArrayList<>();

    private final Fight fight;

    public FightMap(GameMap gameMap, Fight fight) {
        this.model = gameMap;
        this.id = gameMap.getId();
        this.cells = CellLoader.parse(null, gameMap.getData(), null, true);
        this.fight = fight;
    }

    public void register(Creature fighter) {
        this.fighters.add(fighter);
    }

    @Override
    public void out(Creature fighter) {
        this.fighters.remove(fighter);
        fighter.getLocation().getCell().getCreatures().remove(fighter.getId());
        send(GamePacketFormatter.hideCreatureMessage(fighter.getId()));
    }

    @Override
    public void send(String data) {
        this.fighters.forEach(fighter -> fighter.send(data));
    }


    @Override
    public void refreshCreature(Creature creature) {

    }

    @Override
    public void enter(Creature creature) {
        System.err.println("enter");

    }

    @Override
    public void load(Creature creature) {
        System.err.println("Load");
    }

    @Override
    public Creature getCreature(int id) {
        return null;
    }

    @Override
    public String buildData() {
        return FightPacketFormatter.showFighters(fight.fighters());
    }

    @Override
    public byte getWidth() {
        return model.getWidth();
    }

    @Override
    public String getPosition() {
        return model.getPosition();
    }

}
