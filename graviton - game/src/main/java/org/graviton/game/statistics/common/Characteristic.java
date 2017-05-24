package org.graviton.game.statistics.common;


/**
 * Created by Botan on 12/11/2016 : 15:06
 */
public abstract class Characteristic {
    protected short base, equipment, gift, context;

    public Characteristic(short base) {
        this.base = base;
    }

    public void resetBase() {
        this.base = 0;
    }

    public void addBase(short value) {
        this.base += value;
    }

    public void addCoefficientBase(float value) {
        this.base *= value;
    }

    public void addEquipment(short value) {
        this.equipment += value;
    }

    public void addGift(short value) {
        this.gift += value;
    }

    public void addContext(short value) {
        this.context += value;
    }

    public short base() {
        return base;
    }

    public short gift() {
        return gift;
    }

    public short equipment() {
        return equipment;
    }

    public short context() {
        return context;
    }

    public short safeTotal() {
        return (short) (base + equipment + gift);
    }

    public abstract int total();

    void clearBuff() {
        this.context = 0;
    }
}
