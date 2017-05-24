package org.graviton.core.loader;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Botan on 07/12/2016. 14:48
 */
public class FastLoader {

    private final ExecutorService executorService = Executors.newCachedThreadPool(r -> new Thread(r) {{
        setDaemon(true);
        setPriority(MAX_PRIORITY);
    }});

    private final List<Runnable> tasks;

    public FastLoader(Runnable... runnableArray) {
        this.tasks = Arrays.asList(runnableArray);
    }

    public void run(Runnable runnable) {
        this.executorService.execute(runnable);
    }

    public void launch() {
        CompletableFuture.allOf(tasks.stream().map(this::runAsync).toArray(CompletableFuture[]::new)).join();
        executorService.shutdownNow();
    }

    private CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executorService);
    }

}
