package org.graviton.core.injector;

import com.google.inject.AbstractModule;
import org.graviton.core.Program;
import org.graviton.core.injector.modules.ConfigurationModule;
import org.graviton.core.injector.modules.DatabaseModule;
import org.graviton.core.injector.modules.NetworkModule;
import org.graviton.shell.Shell;

/**
 * Created by Botan on 03/11/2016 : 19:27
 */
public class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Program.class).asEagerSingleton();
        bind(Shell.class).asEagerSingleton();

        install(new ConfigurationModule());
        install(new DatabaseModule());
        install(new NetworkModule());
    }
}
