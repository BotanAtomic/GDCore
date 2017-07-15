package org.graviton.game.maps.door;

import lombok.Data;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.network.game.protocol.InteractiveDoorPacketFormatter;
import org.graviton.utils.Utils;
import org.graviton.xml.XMLElement;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Botan on 15/07/17. 20:09
 */

@Data
public class InteractiveDoor {
    private final short id;
    private final GameMap gameMap;
    private final List<Short> requieredCells;
    private int time;
    private boolean open;

    public InteractiveDoor(XMLElement element, GameMap gameMap) {
        this.id = element.getAttribute("id").toShort();
        this.gameMap = gameMap;
        this.time = element.getAttribute("time").toInt();
        this.requieredCells = Utils.arrayToShortList(element.getAttribute("needCells").toString(), ",");
    }

    public void check() {
        System.err.println("Size q = " + requieredCells);
        System.err.println("Size r = " + requieredCells.stream().filter(cell -> !gameMap.getCell(cell).getCreatures().isEmpty()).count());
        if (requieredCells.stream().filter(cell -> !gameMap.getCell(cell).getCreatures().isEmpty()).count() == requieredCells.size())
            open();
    }

    private void open() {
        Cell cell = gameMap.getCell(id);
        cell.setWalkable(true);
        gameMap.send(InteractiveDoorPacketFormatter.cellUpdateMessage(id, true));
        gameMap.send(InteractiveDoorPacketFormatter.doorActionMessage(id, true, false));
        this.open = true;
        gameMap.getEntityFactory().getScheduler().schedule(this::close, time, TimeUnit.SECONDS);
    }

    private void close() {
        Cell cell = gameMap.getCell(id);
        cell.setWalkable(false);
        gameMap.send(InteractiveDoorPacketFormatter.cellUpdateMessage(id, false));
        gameMap.send(InteractiveDoorPacketFormatter.doorActionMessage(id, false, false));
        this.open = false;
    }

}
