package org.graviton.core;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.graviton.api.Manageable;
import org.graviton.script.ScriptProcessor;
import org.graviton.shell.Shell;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Created by Botan on 29/10/2016 : 20:45
 */
@Slf4j
public class Program {
    private final Collection<Manageable> manageable;

    @Inject private Shell shell;

    public Program() {
        this.manageable = new LinkedList<>();
    }

    public void start(long startTime) {
        this.manageable.stream().sorted(Comparator.comparingInt(Manageable::index)).forEach(Manageable::start);
        log.debug("Program successfully started [{} ms]", (System.currentTimeMillis() - startTime));
        shell.begin();
    }

    void stop() {
        this.manageable.stream().sorted(Comparator.comparingInt(Manageable::index)).forEach(Manageable::stop);
        log.debug("Program successfully closed");
    }

    public void register(Manageable manageable) {
        this.manageable.add(manageable);
    }
}
