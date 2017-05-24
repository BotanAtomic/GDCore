package org.graviton.database.repository;

import lombok.extern.slf4j.Slf4j;
import org.graviton.database.Repository;
import org.graviton.game.action.Action;
import org.graviton.game.action.common.GameAction;

import static org.graviton.utils.FastClassLoader.getClasses;

/**
 * Created by Botan on 06/05/17. 22:43
 */

@Slf4j
public class ActionRepository extends Repository<Short, Class<? extends Action>> {

    public int initialize() {
        getClasses("org.graviton.game.action", Action.class).stream().filter(clazz -> clazz.isAnnotationPresent(GameAction.class)).forEach(clazz -> super.put(clazz.getAnnotation(GameAction.class).id(), clazz));
        return super.size();
    }

    @Override public Class<? extends Action> find(Object value) {
        return null;
    }

    public Action create(short id) {
        try {
            return super.get(id).newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
