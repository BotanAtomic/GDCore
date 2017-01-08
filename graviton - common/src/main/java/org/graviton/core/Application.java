package org.graviton.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import org.graviton.function.Header;

/**
 * Created by Botan on 27/11/16.
 */
public class Application {

    public static void create(AbstractModule module, long startTime) {
        Header.build();

        final Program server = Guice.createInjector(module).getInstance(Program.class);

        server.start(startTime);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }

}
