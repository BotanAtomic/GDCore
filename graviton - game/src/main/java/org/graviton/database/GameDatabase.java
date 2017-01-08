package org.graviton.database;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.graviton.core.Program;

import java.util.Properties;

/**
 * Created by Botan on 04/11/2016 : 22:23
 */
public class GameDatabase extends AbstractDatabase {
    @Inject
    public GameDatabase(@Named("database.game.properties") Properties properties, Program program) {
        super(properties, program, true);
    }
}
