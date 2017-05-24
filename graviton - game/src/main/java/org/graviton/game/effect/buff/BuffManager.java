package org.graviton.game.effect.buff;

import org.graviton.game.effect.state.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Botan on 18/04/2017. 01:28
 */
public class BuffManager {
    private List<Buff> buffs = new ArrayList<>();
    private List<State> states = new ArrayList<>();

    public boolean hasState(State neededState) {
        return this.states.stream().filter(state -> state.ordinal() == neededState.ordinal()).count() > 0;
    }

    public void addState(State state) {
        this.states.add(state);
    }

    public void removeState(State state) {
        this.states.remove(state);
    }

    public Buff getBuff(Class<?> buffClass) {
        Optional<Buff> buffOptional = this.buffs.stream().filter(buff -> buff.getClass().equals(buffClass)).findFirst();
        return buffOptional.isPresent() ? buffOptional.get() : null;
    }

    public Collection<Buff> getBuffs(Class<?> buffClass) {
        return this.buffs.stream().filter(buff -> buff.getClass().equals(buffClass)).collect(Collectors.toList());
    }

    public void addBuff(Buff buff) {
        this.buffs.add(buff);
    }

    public void removeBuff(Buff buff) {
        this.buffs.remove(buff);
    }

    public void checkBuffs() {
        this.buffs.forEach(Buff::check);
    }

    public void decrementBuffs() {
        new ArrayList<>(this.buffs).forEach(Buff::decrement);
    }

    public void clearBuffs() {
        new ArrayList<>(this.buffs).forEach(buff -> {
            buff.clear();
            this.buffs.remove(buff);
        });
    }

    public void clear() {
        this.buffs.clear();
        this.states.clear();
    }

}
