package org.graviton.game.creature.collector;

import lombok.Data;
import org.graviton.game.creature.Creature;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.alignment.Alignment;
import org.graviton.game.creature.monster.extra.Double;
import org.graviton.game.fight.Fighter;
import org.graviton.game.guild.Guild;
import org.graviton.game.intelligence.api.ArtificialIntelligence;
import org.graviton.game.inventory.CollectorInventory;
import org.graviton.game.look.AbstractLook;
import org.graviton.game.position.Location;
import org.graviton.game.spell.Spell;
import org.graviton.game.statistics.common.Statistics;
import org.graviton.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.graviton.network.game.protocol.MonsterPacketFormatter.collectorGmMessage;

/**
 * Created by Botan on 09/03/2017. 21:27
 */

@Data
public class Collector extends Fighter implements Creature {
    private final int id;
    private final Guild guild;
    private final Date placeTime;

    private Location location;

    private int[] names;

    private String placer;

    private CollectorInventory inventory;

    private List<Spell> spells;

    public Collector(int id, Guild guild, Location location, String placer) {
        this.id = id;
        this.guild = guild;
        this.location = location;
        this.inventory = new CollectorInventory(this);
        this.names = new int[]{Utils.random(1, 129), Utils.random(1, 227)};
        this.placer = placer;
        this.placeTime = new Date();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return Integer.toString(names[0], 36) + "," + Integer.toString(names[1], 36);
    }

    @Override
    public short getLevel() {
        return guild.getLevel();
    }

    @Override
    public Creature getCreature() {
        return this;
    }

    @Override
    public String getGm() {
        return collectorGmMessage(this);
    }

    @Override
    public void send(String data) {

    }

    @Override
    public String getFightGM() {
        return null;
    }

    @Override
    public String doubleGm(Double clone) {
        return null;
    }

    @Override
    public ArtificialIntelligence artificialIntelligence() {
        return null;
    }

    @Override
    public List<Spell> getSpells() {
        return spells == null ? this.spells = new ArrayList<>(guild.getSpells().values()) : this.spells;
    }

    @Override
    public Alignment getAlignment() {
        return null;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public int getColor(byte color) {
        return 0;
    }

    @Override
    public Statistics getStatistics() {
        return null;
    }

    @Override
    public EntityFactory entityFactory() {
        return null;
    }

    @Override
    public AbstractLook look() {
        return null;
    }
}
