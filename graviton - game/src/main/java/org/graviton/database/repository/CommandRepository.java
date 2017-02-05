package org.graviton.database.repository;

import lombok.extern.slf4j.Slf4j;
import org.graviton.database.Repository;
import org.graviton.game.command.api.AbstractCommand;
import org.graviton.game.command.api.Command;

import static org.graviton.utils.FastClassLoader.getClasses;

/**
 * Created by Botan on 15/01/2017. 16:01
 */

@Slf4j
public class CommandRepository extends Repository<String, AbstractCommand> {

    public int load() {
        for (Class<? extends AbstractCommand> clazz : getClasses("org.graviton.game.command.commands", AbstractCommand.class)) {
            try {
                add(clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("Unable to load command {} -> {}", clazz.getSimpleName(), e);
            }
        }

        return super.objects.size();
    }

    private void add(AbstractCommand command) {
        add(command.getClass().getAnnotation(Command.class).value(), command);
    }

    @Override
    public AbstractCommand find(Object value) {
        return get((String) value);
    }
}
