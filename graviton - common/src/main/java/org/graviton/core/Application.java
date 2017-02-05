package org.graviton.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import org.graviton.function.Header;

/**
 * Created by Botan on 27/11/16. 15:22
 */
public class Application {

    public static void create(AbstractModule module, long startTime) {
        Header.build();

        final Program program = Guice.createInjector(module).getInstance(Program.class);

        program.start(startTime);
        Runtime.getRuntime().addShutdownHook(new Thread(program::stop));
    }

}
