package org.graviton.game.items.common;

/**
 * Created by Botan on 03/12/2016. 20:37
 */
public enum ItemPosition {
    NotEquipped((byte) -1),
    Amulet((byte) 0),
    Weapon((byte) 1, true),
    LeftRing((byte) 2),
    Belt((byte) 3),
    RightRing((byte) 4),
    Boot((byte) 5),
    Hat((byte) 6, true),
    Cloak((byte) 7, true),
    Pet((byte) 8, true),
    Dofus1((byte) 9),
    Dofus2((byte) 10),
    Dofus3((byte) 11),
    Dofus4((byte) 12),
    Dofus5((byte) 13),
    Dofus6((byte) 14),
    Shield((byte) 15, true),
    ItemBar1((byte) 35),
    ItemBar2((byte) 36),
    ItemBar3((byte) 37),
    ItemBar4((byte) 38),
    ItemBar5((byte) 39),
    ItemBar6((byte) 40),
    ItemBar7((byte) 41),
    ItemBar8((byte) 42),
    ItemBar9((byte) 43),
    ItemBar10((byte) 44),
    ItemBar11((byte) 45),
    ItemBar12((byte) 46),
    ItemBar13((byte) 47),
    ItemBar14((byte) 48),
    ItemBar15((byte) 49),
    ItemBar16((byte) 50),
    ItemBar17((byte) 51),
    ItemBar18((byte) 52),
    ItemBar19((byte) 53),
    ItemBar20((byte) 54),
    ItemBar21((byte) 55),
    ItemBar22((byte) 56),
    ItemBar23((byte) 57);

    private byte value;
    private boolean needUpdate = false;

    ItemPosition(byte value) {
        this.value = value;
    }

    ItemPosition(byte value, boolean needUpdate) {
        this.value = value;
        this.needUpdate = needUpdate;
    }

    public static ItemPosition get(byte value) {
        for (ItemPosition itemPosition : values())
            if (itemPosition.value() == value)
                return itemPosition;
        return null;
    }

    public byte value() {
        return value;
    }

    public boolean equipped() {
        return this != NotEquipped && !name().contains("Bar");
    }

    public boolean equippedWithoutBar() {
        return this != NotEquipped;
    }

    public boolean needUpdate() {
        return needUpdate;
    }

}
