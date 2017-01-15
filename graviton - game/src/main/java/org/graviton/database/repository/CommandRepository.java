package org.graviton.database.repository;

import lombok.extern.slf4j.Slf4j;
import org.graviton.database.Repository;
import org.graviton.game.command.AbstractCommand;
import org.graviton.utils.FastClassLoader;

/**
 * Created by Botan on 15/01/2017. 16:01
 */

@Slf4j
public class CommandRepository extends Repository<String, AbstractCommand> {

    public void load() {
        for (Class<?> clazz : FastClassLoader.getClasses("org.graviton.game.command.commands", AbstractCommand.class)) {
            try {
                AbstractCommand command = (AbstractCommand) clazz.newInstance();
                add(command.name(), command);
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("Unable to load command {} -> {}", clazz.getSimpleName(), e);
            }
        }

        log.debug("Successfully load {} commands", super.objects.size());
    }

    @Override
    public AbstractCommand find(Object value) {
        return get((String) value);
    }
}
