package org.graviton.game.effect.state;

/**
 * Created by Botan on 10/01/2017. 21:52
 */
public enum State {
    Neutral((byte) 0),
    Drunk((byte) 1),
    SoulResearcher((byte) 2),
    Carrier((byte) 3),
    Fearful((byte) 4),
    Disoriented((byte) 5),
    Rooted((byte) 6),
    Gravity((byte) 7),
    Carried((byte) 8),
    SylvanMotivation((byte) 9),
    Taming((byte) 10),
    Riding((byte) 11),
    NotWise((byte) 12),
    ReallyNotWise((byte) 13),
    Snowy((byte) 14),
    Awake((byte) 15),
    Fragile((byte) 16),
    Separate((byte) 17),
    Frozen((byte) 18),
    Rift((byte) 19),
    Sleeping((byte) 26),
    Leopardo((byte) 27),
    Free((byte) 28),
    OddGlyph((byte) 29),
    EvenGlyph((byte) 30),
    PrimaryInk((byte) 31),
    SecondaryInk((byte) 32),
    TertiaryInk((byte) 33),
    QuaternaryInk((byte) 34),
    WantToKill((byte) 35),
    WantToParalyze((byte) 36),
    WantToCurse((byte) 37),
    WantToPoison((byte) 38),
    Blur((byte) 39),
    Corrupt((byte) 40),
    Quiet((byte) 41),
    Weak((byte) 42),
    Confused((byte) 48),
    Goulified((byte) 49),
    Altruistic((byte) 50),
    Retirement((byte) 55),
    Invulnerable((byte) 56),
    Countdown2((byte) 57),
    Countdown1((byte) 58),
    Devoted((byte) 60),
    Fighter((byte) 61);

    private final byte value;

    State(byte value) {
        this.value = value;
    }

    public static State get(byte value) {
        for (State state : values())
            if (state.value == value)
                return state;
        return null;
    }

    public byte getValue() {
        return this.value;
    }

}
