package org.graviton.network.game.protocol;

import org.graviton.game.creature.collector.Collector;
import org.graviton.game.creature.monster.Monster;
import org.graviton.game.creature.monster.MonsterGroup;
import org.graviton.game.fight.Fighter;

/**
 * Created by Botan on 03/12/2016. 10:35
 */
public class  MonsterPacketFormatter {

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
            identity.append(monster.getTemplate().getId()).append(',');
            skins.append(monster.getTemplate().getSkin()).append('^').append(monster.getSize()).append(',');
            levels.append(monster.getLevel()).append(',');
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

    public static String fighterCloneGmMessage(Monster monster, Fighter clone) {
        StringBuilder builder = new StringBuilder();
        builder.append("-2;").append(monster.getTemplate().getSkin()).append('^').append(monster.getSize()).append(';');
        builder.append(monster.getGrade()).append(';');
        builder.append(monster.getTemplate().getColors().replace(",", ";")).append(';');
        builder.append("0,0,0,0;");
        builder.append(clone.getLife().getMaximum()).append(';');
        builder.append(clone.getCurrentActionPoint()).append(';');
        builder.append(clone.getCurrentMovementPoint()).append(';');
        builder.append(monster.getTeam().getSide().ordinal()).append(";\n");
        return builder.toString();
    }

    public static String collectorGmMessage(Collector collector) {
        StringBuilder builder = new StringBuilder();
        builder.append(collector.getLocation().getCell().getId()).append(';');
        builder.append(collector.getLocation().getOrientation().ordinal()).append(';');
        builder.append(0).append(';');
        builder.append(collector.getId()).append(';');
        builder.append(Integer.toString(collector.getNames()[0], 36)).append(",").append(Integer.toString(collector.getNames()[1], 36)).append(';');
        builder.append("-6").append(';');
        builder.append("6000^100;");
        builder.append(collector.getGuild().getLevel()).append(';');
        builder.append(collector.getGuild().getName()).append(';').append(collector.getGuild().getEmblem());
        return builder.toString();
    }

}
