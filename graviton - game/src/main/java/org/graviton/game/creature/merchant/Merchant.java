package org.graviton.game.creature.merchant;

import lombok.Data;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.Creature;
import org.graviton.game.inventory.PlayerStore;
import org.graviton.game.look.AbstractLook;
import org.graviton.game.look.enums.Orientation;
import org.graviton.game.position.Location;
import org.graviton.game.statistics.common.Statistics;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Botan on 21/05/17. 10:24
 */

@Data
public class Merchant implements Creature {
    private final PlayerStore store;

    private final Player player;

    private final Location location;

    private AtomicBoolean busy = new AtomicBoolean(false);

    public Merchant(PlayerStore store) {
        this.store = store;
        this.player = store.getPlayer();
        this.location = new Location(player.getMap(), player.getCell().getId(), Orientation.SOUTH);
    }

    public short getSkin() {
        return player.getSkin();
    }

    public short getSize() {
        return player.getSize();
    }

    public String getName() {
        return player.getName();
    }

    @Override
    public int getId() {
        return player.getId();
    }

    @Override
    public String getGm() {
        return PlayerPacketFormatter.merchantGmMessage(this);
    }

    @Override
    public void send(String data) {

    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(Location location) {

    }

    @Override
    public int getColor(byte color) {
        return player.getColor(color);
    }

    @Override
    public Statistics getStatistics() {
        return null;
    }

    @Override
    public EntityFactory entityFactory() {
        return player.getEntityFactory();
    }

    @Override
    public AbstractLook look() {
        return player.getLook();
    }

    public boolean isBusy() {
        return busy.get();
    }
}
