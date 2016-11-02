package org.graviton.core;


import com.google.inject.Guice;
import com.google.inject.Injector;
import org.graviton.core.injector.MainModule;
import org.graviton.function.Header;

/**
 * Created by Botan on 29/10/2016 : 03:09
 */
public class Main {
    private final static long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        Header.build();

        final Injector injector = Guice.createInjector(new MainModule());
        final Program server = injector.getInstance(Program.class);

        server.start(startTime);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }



}
