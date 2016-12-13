package org.graviton.game.creature.monster.extra;

import lombok.Data;
import org.graviton.game.creature.monster.MonsterTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Botan on 13/12/2016. 20:13
 */
@Data
public class ExtraMonster {
    private final List<Short> subArea = new ArrayList<>();
    private MonsterTemplate template;
    private byte chance;

    public ExtraMonster(MonsterTemplate template, byte chance) {
        this.template = template;
        this.chance = chance;
    }

    public void registerSubArea(short area) {
        this.subArea.add(area);
    }

}
