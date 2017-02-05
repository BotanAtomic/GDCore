package org.graviton.game.fight.common;

import lombok.Getter;

/**
 * Created by Botan on 24/12/2016. 01:45
 */
public enum FightAction {
    TELEPORT_EVENT((short) 4),
    PUSH((short) 5),
    MOVEMENT_POINT_EVENT((short) 129),
    LIFE_EVENT((short) 100),
    RETURN_SPELL((short) 106),
    TACKLE((short) 104),
    ACTION_POINT_EVENT((short) 102),
    ARMOR((short) 105),
    RETURN_DAMAGE((short) 107),
    REMOVE_RANGE((short) 116),
    ADD_RANGE((short) 117),
    REMOVE_MOVEMENT_POINT((short) 127),
    ADD_MOVEMENT_POINT((short) 128),
    INVISIBLE_EVENT((short) 150),
    INVOCATION((short) 181),
    USE_SPELL((short) 300),
    CRITICAL_SPELL((short) 301),
    CRITICAL_FAILURE((short) 302),
    WEAPON_ATTACK((short) 303),
    WEAPON_CRITICAL_FAILURE((short) 305),
    DODGE_ACTION_POINT((short) 308),
    DODGE_MOVEMENT_POINT((short) 309),
    STATE_EVENT((short) 950);

    @Getter
    private final short id;

    FightAction(short id) {
        this.id = id;
    }

}
