package org.graviton.core;

import lombok.extern.slf4j.Slf4j;
import org.graviton.api.Manageable;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Botan on 29/10/2016 : 20:45
 */
@Slf4j
public class Server {
    private Collection<Manageable> manageable;

    public Server() {
        this.manageable = new LinkedList<>();
    }

    public void start(long startTime) {
        this.manageable.forEach(Manageable::start);
        log.debug("Program successfully started [{}s]", (System.currentTimeMillis() - startTime) / 1000);
    }

    public void stop() {
        this.manageable.forEach(Manageable::stop);
        log.debug("Program successfully closed");
    }

    public void add(Manageable manageable) {
        this.manageable.add(manageable);
    }
}
