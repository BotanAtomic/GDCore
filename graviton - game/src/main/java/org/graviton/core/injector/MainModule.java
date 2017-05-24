package org.graviton.core.injector;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.graviton.annotation.Scheduler;
import org.graviton.annotation.Worker;
import org.graviton.core.Program;
import org.graviton.core.injector.modules.ConfigurationModule;
import org.graviton.core.injector.modules.RepositoryModule;
import org.graviton.core.injector.modules.NetworkModule;
import org.graviton.script.ScriptProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Botan on 03/11/2016 : 19:27
 */
public class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Program.class).asEagerSingleton();
        bind(ScriptProcessor.class).asEagerSingleton();

        install(new ConfigurationModule());
        install(new RepositoryModule());
        install(new NetworkModule());
    }

    @Provides @Scheduler
    public ScheduledThreadPoolExecutor scheduler() {
        return new ScheduledThreadPoolExecutor(Integer.MAX_VALUE);
    }

    @Provides @Worker
    public ExecutorService worker() {
        return Executors.newCachedThreadPool();
    }
}
