package org.graviton.game.fight.common;

import lombok.Getter;

/**
 * Created by Botan on 24/12/2016. 01:45
 */
public enum FightAction {
    LOOSE_MOVEMENT_POINT((short) 129),
    TACKLE((short) 104),
    LOOSE_ACTION_POINT((short) 102);


    @Getter
    private final short id;

    FightAction(short id) {
        this.id = id;
    }

}
