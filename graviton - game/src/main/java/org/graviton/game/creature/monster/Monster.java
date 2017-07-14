package org.graviton.game.creature.monster;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.Creature;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.alignment.Alignment;
import org.graviton.game.creature.monster.extra.Double;
import org.graviton.game.fight.Fighter;
import org.graviton.game.intelligence.api.ArtificialIntelligence;
import org.graviton.game.look.AbstractLook;
import org.graviton.game.position.Location;
import org.graviton.game.spell.Spell;
import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.common.Statistics;
import org.graviton.game.statistics.type.MonsterStatistics;
import org.graviton.network.game.protocol.MonsterPacketFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Botan on 02/12/2016. 21:34
 */
@Data
public class Monster extends Fighter implements Creature {
    private final MonsterTemplate template;
    private final MonsterStatistics statistics;
    private final byte size;
    private final byte grade;
    private final int baseExperience;
    private int id;
    private Location location;
    private List<Spell> spells;

    public Monster(MonsterTemplate template, byte grade, short level, int baseExperience, String resistance, String life, String statistics, Map<CharacteristicType, Characteristic> characteristics, List<Spell> spells) {
        this.template = template;
        this.size = (byte) (100 + (2 * (grade - 1)));
        this.grade = grade;
        this.baseExperience = baseExperience;
        this.statistics = new MonsterStatistics(level, life, resistance.split("@")[1].split(";"), statistics.split(","), characteristics);
        this.spells = spells;
    }

    private Monster(Monster monster) {
        this.template = monster.getTemplate();
        this.size = monster.getSize();
        this.grade = monster.getGrade();
        this.baseExperience = monster.getBaseExperience();
        this.statistics = (MonsterStatistics) monster.getStatistics().copy();
        this.spells = new ArrayList<>(monster.getSpells());
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getGm(Player player) {
        return MonsterPacketFormatter.fighterGmMessage(this);
    }

    @Override
    public String getName() {
        return String.valueOf(template.getId());
    }

    @Override
    public Statistics getStatistics() {
        return this.statistics;
    }

    public short getLevel() {
        return this.statistics.getLevel();
    }

    @Override
    public Creature getCreature() {
        return this;
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
    public EntityFactory entityFactory() {
        return template.getEntityFactory();
    }

    @Override
    public AbstractLook look() {
        return null;
    }

    @Override
    public String getFightGM() {
        return MonsterPacketFormatter.fighterGmMessage(this);
    }

    @Override
    public String doubleGm(Double clone) {
        return MonsterPacketFormatter.fighterCloneGmMessage(this, clone);
    }

    @Override
    public ArtificialIntelligence artificialIntelligence() {
        return entityFactory().getIntelligenceRepository().create(template.getArtificialIntelligence(), this);
    }

    @Override
    public List<Spell> getSpells() {
        return this.spells;
    }

    @Override
    public Alignment getAlignment() {
        return template.getAlignment();
    }

    public Monster copy() {
        return new Monster(this);
    }

    public void applyFighterStatistics(Fighter fighter) {
        float coefficient = (1.0F + (fighter.getLevel()) / 100.0F);

        getStatistics().getLife().addMaximum((int) (getStatistics().getLife().getMaximum() * coefficient));
        getStatistics().getLife().set(getStatistics().getLife().getMaximum());

        getStatistics().get(CharacteristicType.Strength).addCoefficientBase(coefficient);
        getStatistics().get(CharacteristicType.Intelligence).addCoefficientBase(coefficient);
        getStatistics().get(CharacteristicType.Agility).addCoefficientBase(coefficient);
        getStatistics().get(CharacteristicType.Wisdom).addCoefficientBase(coefficient);
        getStatistics().get(CharacteristicType.Chance).addCoefficientBase(coefficient);
    }
}
