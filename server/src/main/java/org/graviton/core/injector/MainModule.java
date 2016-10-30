package org.graviton.core.injector;

import com.google.inject.AbstractModule;
import org.graviton.core.Program;
import org.graviton.core.injector.modules.ConfigurationModule;
import org.graviton.core.injector.modules.DatabaseModule;
import org.graviton.core.injector.modules.NetworkModule;

/**
 * Created by Botan on 29/10/2016 : 06:53
 */
public class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Program.class).asEagerSingleton();

        install(new ConfigurationModule());
        install(new DatabaseModule());
        install(new NetworkModule());
    }
}
