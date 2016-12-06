package org.graviton.game.items.common;

/**
 * Created by Botan on 03/12/2016. 20:37
 */
public enum ItemPosition {
    NotEquipped((byte) -1),
    Amulet((byte) 0),
    Weapon((byte) 1),
    LeftRing((byte) 2),
    Belt((byte) 3),
    RightRing((byte) 4),
    Boot((byte) 5),
    Hat((byte) 6),
    Cloak((byte) 7),
    Pet((byte) 8),
    Dofus1((byte) 9),
    Dofus2((byte) 10),
    Dofus3((byte) 11),
    Dofus4((byte) 12),
    Dofus5((byte) 13),
    Dofus6((byte) 14),
    Shield((byte) 15),
    ItemBar1((byte) 23),
    ItemBar2((byte) 24),
    ItemBar3((byte) 25),
    ItemBar4((byte) 26),
    ItemBar5((byte) 27),
    ItemBar6((byte) 28),
    ItemBar7((byte) 29),
    ItemBar8((byte) 30),
    ItemBar9((byte) 31),
    ItemBar10((byte) 32),
    ItemBar11((byte) 33),
    ItemBar12((byte) 34),
    ItemBar13((byte) 35),
    ItemBar14((byte) 36);

    private byte value;

    ItemPosition(byte value) {
        this.value = value;
    }

    public static ItemPosition get(byte value) {
        return ItemPosition.values()[value + 1];
    }

    public byte value() {
        return value;
    }

    public boolean equipped() {
        return this != NotEquipped && !(value >= 23 && value <= 36);
    }

}
