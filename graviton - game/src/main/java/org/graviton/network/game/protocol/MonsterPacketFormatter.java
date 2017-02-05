package org.graviton.network.game.protocol;

import org.graviton.game.creature.monster.Monster;
import org.graviton.game.creature.monster.MonsterGroup;

/**
 * Created by Botan on 03/12/2016. 10:35
 */
public class MonsterPacketFormatter {

    public static String gmMessage(MonsterGroup monsterGroup) {
        StringBuilder identity = new StringBuilder();
        StringBuilder skins = new StringBuilder();
        StringBuilder levels = new StringBuilder();
        StringBuilder colors = new StringBuilder();
        StringBuilder builder = new StringBuilder();

        builder.append(monsterGroup.getCell()).append(';');
        builder.append(monsterGroup.getOrientation().ordinal()).append(';');
        builder.append(monsterGroup.getStarsPercent()).append(';');
        builder.append(monsterGroup.getId()).append(';');


        monsterGroup.getMonsters().forEach(monster -> {
            identity.append(monster.getTemplate().getId()).append(",");
            skins.append(monster.getTemplate().getSkin()).append(",");
            levels.append(monster.getLevel()).append(",");
            colors.append(monster.getTemplate().getColors()).append(";0,0,0,0;");
        });

        builder.append(substringMinusOne(identity)).append(";-3;");
        builder.append(substringMinusOne(skins)).append(';');
        builder.append(substringMinusOne(levels)).append(';').append(colors);
        return builder.toString();
    }

    private static String substringMinusOne(StringBuilder builder) {
        if (builder.length() == 0)
            return "";
        return builder.substring(0, builder.length() - 1);
    }

    public static String fighterGmMessage(Monster monster) {
        StringBuilder builder = new StringBuilder();
        builder.append("-2;");
        builder.append(monster.getTemplate().getSkin()).append('^').append(monster.getSize()).append(';');
        builder.append(monster.getGrade()).append(';');
        builder.append(monster.getTemplate().getColors().replace(",", ";")).append(';');
        builder.append("0,0,0,0;");
        builder.append(monster.getLife().getMaximum()).append(';');
        builder.append(monster.getCurrentActionPoint()).append(';');
        builder.append(monster.getCurrentMovementPoint()).append(';');
        builder.append(monster.getTeam().getSide().ordinal()).append(";\n");
        return builder.toString();
    }

}
