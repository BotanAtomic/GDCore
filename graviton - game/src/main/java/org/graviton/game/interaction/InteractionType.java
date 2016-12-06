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
    HOUSE_ACTION((short) 507);

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
