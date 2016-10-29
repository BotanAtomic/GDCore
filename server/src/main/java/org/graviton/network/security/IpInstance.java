package org.graviton.network.security;


import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Botan on 14/04/2016.
 */
@Data
public final class IpInstance {
    private AtomicInteger connection;
    private AtomicLong lastConnection;
    private int warning;

    private boolean attacker = false;
    private boolean banned = false;

    public IpInstance() {
        this.lastConnection = new AtomicLong(0);
        this.connection = new AtomicInteger(0);
        this.warning = 0;
    }

    public boolean addWarning() {
        warning++;
        return (warning >= 3) ? (banned = true) : false;
    }
}
