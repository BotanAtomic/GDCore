package org.graviton.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.graviton.function.Header;

/**
 * Created by kurdistan on 27/11/16.
 */
public class Application {

    public static void create(AbstractModule module, long startTime) {
        Header.build();

        final Injector injector = Guice.createInjector(module);
        final Program server = injector.getInstance(Program.class);

        server.start(startTime);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }

}
