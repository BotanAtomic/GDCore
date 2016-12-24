package org.graviton.game.interaction;

/**
 * Created by Botan on 16/11/2016 : 19:22
 */
public enum InteractionType {
    MOVEMENT((short) 1),
    QUEST_SIGN((short) 34),
    SPELL_ATTACK((short) 300),
    WEAPON_ATTACK((short) 303),
    MAP_ACTION((short) 500),
    HOUSE_ACTION((short) 507),
    ASK_DEFY((short) 900),
    ACCEPT_DEFY((short) 901),
    CANCEL_DEFY((short) 902),
    JOIN_FIGHT((short) 903),
    UNKNOWN((short) 0);

    private final short id;

    InteractionType(short id) {
        this.id = id;
    }

    public static InteractionType get(short id) {
        for (InteractionType interactionType : values())
            if (interactionType.id == id)
                return interactionType;
        return null;
    }

}
