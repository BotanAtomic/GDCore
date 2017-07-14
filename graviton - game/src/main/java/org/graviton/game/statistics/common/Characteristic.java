package org.graviton.game.statistics.common;


/**
 * Created by Botan on 12/11/2016 : 15:06
 */
public abstract class Characteristic {
    protected int base, equipment, gift, context;

    public Characteristic(int base) {
        this.base = base;
    }

    public void resetBase() {
        this.base = 0;
    }

    public void addBase(int value) {
        this.base += value;
    }

    public void addCoefficientBase(float value) {
        this.base *= value;
    }

    public void addEquipment(int value) {
        this.equipment += value;
    }

    public void addGift(int value) {
        this.gift += value;
    }

    public void addContext(int value) {
        this.context += value;
    }

    public int base() {
        return base;
    }

    public int gift() {
        return gift;
    }

    public int equipment() {
        return equipment;
    }

    public int context() {
        return context;
    }

    public int safeTotal() {
        return (int) (base + equipment + gift);
    }

    public abstract int total();

    void clearBuff() {
        this.context = 0;
    }
}
