package org.graviton.core.injector;

import com.google.inject.AbstractModule;
import org.graviton.core.Program;

/**
 * Created by Botan on 03/11/2016 : 19:27
 */
public class DefaultModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Program.class).asEagerSingleton();
    }
}
