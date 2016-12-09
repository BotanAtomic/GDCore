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

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final List<Runnable> tasks;

    public FastLoader(Runnable... runnableArray) {
        this.tasks = Arrays.asList(runnableArray);
    }

    public void launch() {
        CompletableFuture.allOf(tasks.stream()
                .map(runnable -> CompletableFuture.runAsync(runnable, executorService)).toArray(CompletableFuture[]::new)).join();
    }

}
