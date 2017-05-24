package org.graviton.database.repository;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.graviton.database.Repository;
import org.graviton.game.command.api.AbstractCommand;
import org.graviton.script.ScriptProcessor;

/**
 * Created by Botan on 15/01/2017. 16:01
 */

@Slf4j
public class CommandRepository extends Repository<String, AbstractCommand> {
    @Inject private ScriptProcessor scriptProcessor;

    public int load() {
        scriptProcessor.importElement(this, "commandRepository");
        scriptProcessor.loadPath("scripts/commands");
        return super.size();
    }

    public void register(AbstractCommand abstractCommand) {
        add(abstractCommand.name(), abstractCommand);
    }

    public void clear() {
        super.clear();
    }


    @Override
    public AbstractCommand find(Object value) {
        return get((String) value);
    }
}
