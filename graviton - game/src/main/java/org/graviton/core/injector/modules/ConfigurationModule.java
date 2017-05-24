package org.graviton.core.injector.modules;

import com.google.inject.AbstractModule;
import lombok.extern.slf4j.Slf4j;
import org.graviton.database.api.GameDatabaseProperties;
import org.graviton.database.api.LoginDatabaseProperties;
import org.graviton.injector.PropertiesBinder;

import java.io.IOException;
import java.util.Properties;

import static org.graviton.utils.Utils.parseComplexDatabaseProperties;

/**
 * Created by Botan on 04/11/2016 : 22:27
 */

@Slf4j
public class ConfigurationModule extends AbstractModule {
    @Override
    protected void configure() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));

            PropertiesBinder.bind(binder(), properties);

            bind(Properties.class).annotatedWith(GameDatabaseProperties.class).toInstance(parseComplexDatabaseProperties(properties, "game"));
            bind(Properties.class).annotatedWith(LoginDatabaseProperties.class).toInstance(parseComplexDatabaseProperties(properties, "login"));

            log.debug("Configuration file loaded");
        } catch (IOException e) {
            super.addError(e);
        }
    }

}
