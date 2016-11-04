package org.graviton.core.injector.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import lombok.extern.slf4j.Slf4j;
import org.graviton.injector.PropertiesBinder;

import java.io.IOException;
import java.util.Properties;

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

            bind(Properties.class).annotatedWith(Names.named("database.game.properties")).toInstance(createDatabaseProperties(properties, "game"));
            bind(Properties.class).annotatedWith(Names.named("database.login.properties")).toInstance(createDatabaseProperties(properties, "login"));

            log.debug("Configuration file successfully loaded");
        } catch (IOException e) {
            super.addError(e);
        }
    }

    private Properties createDatabaseProperties(Properties properties, String database) {
        return new Properties() {{
            properties.keySet().stream().filter(key -> key.toString().startsWith(database.concat(".dataSource"))).forEach(selectedKey ->
                    put(String.valueOf(selectedKey).split(database.concat("."))[1], properties.get(selectedKey))
            );
        }};
    }
}
