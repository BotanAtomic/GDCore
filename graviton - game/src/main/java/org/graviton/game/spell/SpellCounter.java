package org.graviton.game.spell;

import javafx.util.Pair;
import lombok.Getter;

import java.util.*;

/**
 * Created by Botan on 12/04/2017. 23:45
 */

@Getter
public class SpellCounter {

    private Map<Short, Short> spellBoost = new HashMap<>();
    private Map<Short, Byte> spellTime = new HashMap<>();
    private Map<Short, Byte> launchedSpells = new HashMap<>();
    private Map<Short, Map<Integer, Byte>> spellsTarget = new HashMap<>();

    void addLaunchedSpell(short spell, int target) {
        this.launchedSpells.put(spell, (byte) (this.launchedSpells.getOrDefault(spell, (byte) 0) + 1));

        if (target != -1) {
            this.spellsTarget.computeIfAbsent(spell, function -> new HashMap<>());
            spellsTarget.get(spell).put(target, (byte) (spellsTarget.get(spell).getOrDefault(target, (byte) 0) + 1));
        }
    }

    public short getSpellBoostCount(short spell) {
        return this.spellBoost.getOrDefault(spell, (short) 0);
    }

    private byte spellCountByTarget(short spell, int target) {
        if (target == -1)
            return Byte.MIN_VALUE;

        return this.spellsTarget.get(spell) != null && this.spellsTarget.get(spell).get(target) != null ? this.spellsTarget.get(spell).get(target) : 0;
    }

    public void removeSpellBoost(short spellTemplate) {
        this.spellBoost.remove(spellTemplate);
    }

    public void addSpellBoost(short spellTemplate, short boost) {
        this.spellBoost.put(spellTemplate, (short) (boost + this.spellBoost.getOrDefault(spellTemplate, (short) 0)));
    }

    public boolean canLaunchSpell(short spell, byte limitPerTurn, int target, byte limitPerTarget) {
        return !this.spellTime.containsKey(spell) && !(limitPerTurn > 0 && (limitPerTurn <= countOfLaunchedSpell(spell)) || limitPerTarget > 0 && (limitPerTarget <= spellCountByTarget(spell, target)));
    }

    public void decrementSpellTime() {
        new HashMap<>(this.spellTime).forEach((spell, time) -> {
            if (time <= 1)
                this.spellTime.remove(spell);
            else
                this.spellTime.replace(spell, (byte) (time - 1));
        });
    }

    public boolean hasLaunchedSpell(short spell) {
        return this.launchedSpells.containsKey(spell);
    }

    private byte countOfLaunchedSpell(short spell) {
        return this.launchedSpells.getOrDefault(spell, (byte) 0);
    }

    public void resetPerTurn() {
        this.spellsTarget.clear();
        this.launchedSpells.clear();
    }

    public void reset() {
        this.spellsTarget.clear();
        this.launchedSpells.clear();
        this.spellTime.clear();
        this.spellBoost.clear();
    }


}
