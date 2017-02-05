package org.graviton.game.statistics;

import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.common.Statistics;

/**
 * Created by Botan on 26/12/2016. 01:50
 */


public class Life {
    private final Statistics statistics;
    private int current;
    private int maximum;

    public Life(Statistics statistics, int current, int maximum) {
        this.statistics = statistics;
        this.current = current;
        this.maximum = maximum;
    }

    public void setPercent(short percent) {
        double coefficient = percent / 100F;
        set((int) (getMaximum() * coefficient));
    }

    public int getMaximum() {
        return this.maximum + statistics.get(CharacteristicType.Life).total() + statistics.get(CharacteristicType.Vitality).total();
    }

    public int getCurrent() {
        return this.current;
    }

    public void set(int life) {
        this.current = life;
    }

    public void addMaximum(int life) {
        this.maximum += life;
    }

    public void add(int life) {
        this.current += life;

        if (current > getMaximum())
            current = getMaximum();
    }

    public void remove(int life) {
        this.current -= life;

        if (current < 0)
            current = 0;
    }

    public void regenMax() {
        this.current = getMaximum();
    }

}
