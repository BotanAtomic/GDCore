package org.graviton.game.creature.monster.extra;

import lombok.Data;
import org.graviton.game.creature.Creature;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.alignment.Alignment;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.monster.Monster;
import org.graviton.game.fight.Fighter;
import org.graviton.game.intelligence.api.ArtificialIntelligence;
import org.graviton.game.intelligence.artificial.BlockIntelligence;
import org.graviton.game.look.AbstractLook;
import org.graviton.game.position.Location;
import org.graviton.game.spell.Spell;
import org.graviton.game.statistics.common.Statistics;
import org.graviton.game.statistics.type.MonsterStatistics;
import org.graviton.game.statistics.type.PlayerStatistics;

import java.util.List;

/**
 * Created by Botan on 09/02/2017. 18:56
 */

@Data
public class Double extends Fighter implements Creature {
    private final Fighter model;
    private final int id;

    private Location location;

    private final Statistics statistics;

    public Double(Fighter model, int id) {
        this.model = model;
        this.id = id;
        this.statistics = model instanceof Player ? new PlayerStatistics((Player) model) : new MonsterStatistics((Monster) model);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getGm(Player player) {
        return null;
    }

    @Override
    public String getName() {
        return model.getName();
    }

    @Override
    public short getLevel() {
        return model.getLevel();
    }

    @Override
    public Creature getCreature() {
        return this;
    }

    @Override
    public Statistics getStatistics() {
        return statistics;
    }

    @Override
    public EntityFactory entityFactory() {
        return model.getCreature().entityFactory();
    }

    @Override
    public AbstractLook look() {
        return model.getCreature().look();
    }

    @Override
    public void send(String data) {

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
    public String getFightGM() {
        return model.doubleGm(this);
    }

    @Override
    public String doubleGm(Double clone) {
        return "";
    }

    @Override
    public ArtificialIntelligence artificialIntelligence() {
        return new BlockIntelligence(this);
    }

    @Override
    public List<Spell> getSpells() {
        return null;
    }

    @Override
    public Alignment getAlignment() {
        return null;
    }
}
