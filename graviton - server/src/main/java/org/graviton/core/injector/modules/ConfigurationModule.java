package org.graviton.core.injector.modules;

import com.google.inject.AbstractModule;
import lombok.extern.slf4j.Slf4j;
import org.graviton.injector.PropertiesBinder;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Botan on 29/10/2016 : 03:12
 */

@Slf4j
public class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));

            PropertiesBinder.bind(binder(), properties);

            bind(Properties.class).toInstance(new Properties() {{
                properties.keySet().stream().filter(key -> key.toString().contains("dataSource")).forEach(selectedKey -> put(selectedKey, properties.get(selectedKey)));
            }});

            log.debug("Configuration file successfully loaded");
        } catch (IOException e) {
            super.addError(e);
        }
    }


}
