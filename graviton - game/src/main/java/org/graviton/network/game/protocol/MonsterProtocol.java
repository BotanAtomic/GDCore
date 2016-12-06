package org.graviton.network.game.protocol;

import org.graviton.game.creature.monster.MonsterGroup;

/**
 * Created by Botan on 03/12/2016. 10:35
 */
public class MonsterProtocol {

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

        builder.append(identity.toString().substring(0, identity.length() - 1)).append(";-3;");
        builder.append(skins.toString().substring(0, skins.length() - 1)).append(';');
        builder.append(levels.toString().substring(0, levels.length() - 1)).append(';').append(colors);
        return builder.toString();
    }

}
