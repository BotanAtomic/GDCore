package org.graviton.game.action.npc;

import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;
import org.graviton.game.creature.monster.Monster;
import org.graviton.game.creature.monster.MonsterGroup;
import org.graviton.game.creature.monster.MonsterTemplate;
import org.graviton.network.game.GameClient;

import java.util.Collections;

/**
 * Created by Botan on 14/07/17. 23:54
 */

@GameAction(id = 544)
public class Fight implements Action{

    @Override
    public void apply(GameClient client, Object data) {
        MonsterTemplate monsterTemplate = client.getEntityFactory().getMonsterTemplate(Short.parseShort(((String) data).split(",")[0]));
        Monster monster = monsterTemplate.getByLevel(Short.parseShort(((String) data).split(",")[1])).copy();

        MonsterGroup monsterGroup = new MonsterGroup(0, client.getPlayer().getGameMap(), (short) 0, Collections.singleton(monster));

        client.getPlayer().getGameMap().getFightFactory().newMonsterFight(client.getPlayer(), monsterGroup, false);
    }

    @Override
    public void finish() {

    }
}
