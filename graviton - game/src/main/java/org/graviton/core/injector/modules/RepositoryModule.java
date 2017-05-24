package org.graviton.core.injector.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import org.graviton.core.Program;
import org.graviton.database.Database;
import org.graviton.database.api.GameDatabaseProperties;
import org.graviton.database.api.LoginDatabaseProperties;
import org.graviton.database.entity.EntityFactory;
import org.graviton.database.repository.*;

import java.util.Properties;

/**
 * Created by Botan on 04/11/2016 : 22:12
 */
public class RepositoryModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Database.class).annotatedWith(org.graviton.database.api.GameDatabase.class).to(GameDatabase.class).asEagerSingleton();
        bind(Database.class).annotatedWith(org.graviton.database.api.LoginDatabase.class).to(LoginDatabase.class).asEagerSingleton();

        bind(PlayerRepository.class).asEagerSingleton();
        bind(AccountRepository.class).asEagerSingleton();
        bind(GameMapRepository.class).asEagerSingleton();
        bind(CommandRepository.class).asEagerSingleton();
        bind(ArtificialIntelligenceRepository.class).asEagerSingleton();
        bind(GuildRepository.class).asEagerSingleton();
        bind(ActionRepository.class).asEagerSingleton();

        bind(EntityFactory.class).asEagerSingleton();
    }


    private static final class GameDatabase extends Database {

        @Inject private GameDatabase(@GameDatabaseProperties Properties properties, Program program) {
            super(properties, program);
        }
    }

    private static final class LoginDatabase extends Database {

        @Inject private LoginDatabase(@LoginDatabaseProperties Properties properties, Program program) {
            super(properties, program);
        }
    }
}
