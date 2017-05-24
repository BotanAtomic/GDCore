package org.graviton.core.injector.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import org.graviton.core.Program;
import org.graviton.database.Database;
import org.graviton.database.api.LoginDatabase;
import org.graviton.database.api.LoginDatabaseProperties;
import org.graviton.injector.PropertiesBinder;
import org.graviton.utils.Utils;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Botan on 29/10/2016 : 03:12
 */

@Slf4j
public class ConfigurationModule extends AbstractModule {

    private Properties baseProperties;
    private Database database;

    @Override
    protected void configure() {
        Properties properties = this.baseProperties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
            PropertiesBinder.bind(binder(), properties);
            log.debug("configuration file loaded");
        } catch (IOException e) {
            super.addError(e);
        }
    }

    @Provides @LoginDatabase
    public Database mainDatabase(Program program) {
        return database == null ? database = new Database(Utils.parseDatabaseProperties(baseProperties, "dataSource"), program) : database;
    }


}
