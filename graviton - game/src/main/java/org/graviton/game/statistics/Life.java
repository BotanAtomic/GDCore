package org.graviton.game.statistics;

import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.common.Statistics;
import org.joda.time.Interval;

import java.util.Date;

/**
 * Created by Botan on 26/12/2016. 01:50
 */


public class Life {
    private final Statistics statistics;
    private int current;
    private int maximum;

    private Date regenTime;
    private byte regenSpeed = 2;

    private boolean blockRegeneration = false;

    public Life(Statistics statistics, int current, int maximum, boolean blockRegeneration) {
        this.statistics = statistics;
        this.current = current;
        this.maximum = maximum;
        this.blockRegeneration = blockRegeneration;
    }

    public Life(Statistics statistics, byte percent, int maximum, boolean blockRegeneration, Date regenTime) {
        this.statistics = statistics;
        this.maximum = maximum;
        setPercent(percent);
        this.blockRegeneration = blockRegeneration;
        this.regenTime = regenTime;
        applyRegenTime();
    }

    public void applyRegenTime() {
        if (!blockRegeneration) {
            long second = new Interval(regenTime == null ? new Date().getTime() : regenTime.getTime(), new Date().getTime()).toDurationMillis() / 1000;
            add((int) (second / regenSpeed));
            refreshRegenTime();
        }
    }

    public void refreshRegenTime() {
        regenTime = new Date();
    }

    public void setPercent(short percent) {
        double coefficient = percent / 100F;
        set((int) (getMaximum() * coefficient));
    }

    public byte getPercent() {
        return (byte) (getCurrent() / getMaximum() * 100F);
    }

    public int getMaximum() {
        return this.maximum + statistics.get(CharacteristicType.Life).total() + statistics.get(CharacteristicType.Vitality).total();
    }

    public int getSafeMaximum() {
        return this.maximum + statistics.get(CharacteristicType.Life).safeTotal() + statistics.get(CharacteristicType.Vitality).safeTotal();
    }

    public int getCurrent() {
        applyRegenTime();
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

    public void blockRegeneration(boolean blockRegeneration) {
        this.blockRegeneration = blockRegeneration;

        if (!blockRegeneration)
            this.regenTime = new Date();
    }

}
