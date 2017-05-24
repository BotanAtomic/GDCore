package org.graviton.core.injector;

import com.google.inject.AbstractModule;
import org.graviton.core.Program;
import org.graviton.core.injector.modules.ConfigurationModule;
import org.graviton.core.injector.modules.NetworkModule;
import org.graviton.core.injector.modules.RepositoryModule;
import org.graviton.shell.Shell;

/**
 * Created by Botan on 29/10/2016 : 06:53
 */
public class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Shell.class).asEagerSingleton();
        bind(Program.class).asEagerSingleton();

        install(new ConfigurationModule());
        install(new NetworkModule());
        install(new RepositoryModule());
    }
}
