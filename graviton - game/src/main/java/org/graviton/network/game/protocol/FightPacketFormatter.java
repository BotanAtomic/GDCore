package org.graviton.network.game.protocol;

import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.fight.common.FightType;

import java.util.Collection;

/**
 * Created by Botan on 11/12/2016. 02:37
 */
public class FightPacketFormatter {

    public static String newFightMessage(byte canCancel, int remainingTime, FightType fightType) {
        return "GJK2" + '|' + canCancel + '|' + (fightType == FightType.DUEL ? '1' : '0') + "|1" + '|' + remainingTime + '|' + fightType.ordinal();
    }

    public static String startCellsMessage(String challengersPlaces, String defendersPlaces, FightSide team) {
        return "GP" + challengersPlaces + "|" + defendersPlaces + "|" + team.ordinal();
    }

    public static String showFighters(Collection<Fighter> fighters) {
        StringBuilder builder = new StringBuilder();
        fighters.forEach(fighter -> {
            builder.append("GM|+");
            builder.append(fighter.getFightCell().getId()).append(';');
            builder.append("1;0;");//1; = Orientation
            builder.append(fighter.getId()).append(';');
            builder.append(fighter.getName()).append(';');
            builder.append(fighter.getFightGM());
        });
        return builder.toString();
    }
}
