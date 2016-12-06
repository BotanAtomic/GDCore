package org.graviton.game.items.common;

/**
 * Created by Botan on 03/12/2016. 20:27
 */
public enum ItemType {
    Amulet((byte) 1, true),
    Bow((byte) 2, true),
    Wand((byte) 3, true),
    Staff((byte) 4, true),
    Dagger((byte) 5, true),
    Sword((byte) 6, true),
    Hammer((byte) 7, true),
    Shovel((byte) 8, true),
    Ring((byte) 9, true),
    Belt((byte) 10, true),
    Boot((byte) 11, true),
    Potion((byte) 12),
    ExperienceParchment((byte) 13),
    Gift((byte) 14),
    Resource((byte) 15),
    Hat((byte) 16, true),
    Cloack((byte) 17, true),
    Pet((byte) 18, true),
    Axe((byte) 19, true),
    Tool((byte) 20, true),
    Pickaxe((byte) 21, true),
    Scythe((byte) 22, true),
    Dofus((byte) 23, true),
    Quest((byte) 24),
    Document((byte) 25),
    AlchemyPotion((byte) 26),
    Transform((byte) 27),
    BoostFood((byte) 28),
    Benediction((byte) 29),
    Malediction((byte) 30),
    RolePlayGift((byte) 31),
    Follower((byte) 32),
    Bread((byte) 33),
    Cereal((byte) 34),
    Flower((byte) 35),
    Plant((byte) 36),
    Beer((byte) 37),
    Wood((byte) 38),
    Ore((byte) 39),
    Alloy((byte) 40),
    Fish((byte) 41),
    Candy((byte) 42),
    ForgetPotion((byte) 43),
    JobPotion((byte) 44),
    SpellPotion((byte) 45),
    Fruit((byte) 46),
    Bone((byte) 47),
    Powder((byte) 48),
    ComestibleFish((byte) 49),
    PreciousStone((byte) 50),
    Stone((byte) 51),
    Flour((byte) 52),
    Feather((byte) 53),
    Hair((byte) 54),
    Fabric((byte) 55),
    Leather((byte) 56),
    Wool((byte) 57),
    Seed((byte) 58),
    Skin((byte) 59),
    Oil((byte) 60),
    StuffedToy((byte) 61),
    GuttedFish((byte) 62),
    Meat((byte) 63),
    PreservedMeat((byte) 64),
    Tail((byte) 65),
    Metaria((byte) 66),
    Vegetable((byte) 68),
    ComestibleMeat((byte) 69),
    Dye((byte) 70),
    AlchemyEquipment((byte) 71),
    PetEgg((byte) 72),
    WeaponControl((byte) 73),
    FeeArtifice((byte) 74),
    SpellParchment((byte) 75),
    StatParchment((byte) 76),
    KennelCertificate((byte) 77),
    SmithMagicRune((byte) 78),
    Drink((byte) 79),
    QuestObject((byte) 80),
    Backpack((byte) 81, true),
    Shield((byte) 82, true),
    Soulstone((byte) 83, true),
    Key((byte) 84),
    FullSoulstone((byte) 85),
    PercepteurForgetPotion((byte) 86),
    PARCHO_RECHERCHE((byte) 87),
    MagicStone((byte) 88),
    Gifts((byte) 89),
    GhostPet((byte) 90),
    Dragodinde((byte) 91, true),
    Bouftou((byte) 92),
    BreedingObject((byte) 93),
    UsableObject((byte) 94),
    Plank((byte) 95),
    Bark((byte) 96),
    DragodindeCertificate((byte) 97),
    Root((byte) 98),
    CatchNet((byte) 99),
    ResourceBag((byte) 100),
    Crossbow((byte) 102, true),
    Paw((byte) 103),
    Wing((byte) 104),
    Egg((byte) 105),
    Ear((byte) 106),
    Carapace((byte) 107),
    Bud((byte) 108),
    Eye((byte) 109),
    Jelly((byte) 110),
    Shell((byte) 111),
    Prism((byte) 112),
    Obvijevan((byte) 113, true),
    MagicWeapon((byte) 114, true),
    ShushuSoulPiece((byte) 115),
    PetPotion((byte) 116);

    private byte value;
    private boolean equipment = false;

    ItemType(byte value) {
        this.value = value;
    }

    ItemType(byte value, boolean equipment) {
        this.value = value;
        this.equipment = equipment;
    }

    public static ItemType get(byte id) {
        for (ItemType type : values())
            if (type.value == id)
                return type;
        return null;
    }

    public boolean isWeapon() {
        switch (this) {
            case Bow:
            case Wand:
            case Staff:
            case Dagger:
            case Sword:
            case Hammer:
            case Shovel:
            case Axe:
            case Tool:
            case Pickaxe:
            case Scythe:
            case Crossbow:
            case Soulstone:
            case MagicWeapon:
                return true;
        }
        return false;
    }

    public boolean isEquipment() {
        return this.equipment;
    }
}
