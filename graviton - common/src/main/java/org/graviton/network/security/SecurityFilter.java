package org.graviton.network.security;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Botan on 03/04/2016 : 03:12
 */
@Slf4j
public class SecurityFilter extends IoFilterAdapter {

    private final ReentrantLock locker = new ReentrantLock();

    private final byte maxConnection;

    private final Map<String, Instance> instances;

    /**
     * @param maxConnection Set max connection per second
     */
    public SecurityFilter(byte maxConnection) {
        this.maxConnection = maxConnection;
        this.instances = new ConcurrentHashMap<>();
    }

    private boolean checkAccess(Instance instance, long session) {
        if (instance.isBanned()) {
            instance.blockSession(session);
            return false;
        }

        locker.lock();

        try {
            if ((System.currentTimeMillis() - instance.setAndGetLastConnection(System.currentTimeMillis())) < 1000) {
                if (instance.getConnections().incrementAndGet() > this.maxConnection) {
                    instance.addWarning();
                    instance.blockSession(session);
                    log.debug("[Session {}] Connection refused ({})", session, instance.getAddress());
                    return false;
                } else {
                    instance.getConnections().set(0);
                    return true;
                }
            }

            instance.getConnections().set(0);
        } finally {
            locker.unlock();
        }
        return true;
    }

    private Instance get(String address) {
        Instance instance = this.instances.get(address);
        return instance != null ? instance : addAndGet(new Instance(address));
    }

    private Instance addAndGet(Instance instance) {
        this.instances.put(instance.getAddress(), instance);
        return instance;
    }

    @Override
    public void sessionCreated(NextFilter nextFilter, IoSession session) {
        if (checkAccess(get(session.getRemoteAddress().toString().split(":")[0].substring(1)), session.getId()))
            nextFilter.sessionCreated(session);
        else
            session.closeNow();
    }

    @Override
    public void sessionOpened(NextFilter nextFilter, IoSession session) throws Exception {
        if (!get(session.getRemoteAddress().toString().split(":")[0].substring(1)).isBlocked(session.getId()))
            nextFilter.sessionOpened(session);
        else
            session.closeNow();

    }

    @Override
    public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
        session.closeNow();
    }

    @Override
    public void sessionIdle(NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
        nextFilter.sessionIdle(session, status);
    }

    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) {
        nextFilter.messageReceived(session, message);
    }

    @Override
    public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        nextFilter.messageSent(session, writeRequest);
    }

    @Data
    private static class Instance {
        private final String address;
        private final AtomicInteger connections = new AtomicInteger(0);
        private final List<Long> blockedSessions = new CopyOnWriteArrayList<>();
        private long lastConnection = 0;
        private boolean banned = false;
        private short warning;

        public Instance(String address) {
            this.address = address;
        }

        public void addWarning() {
            banned = (warning++ >= 2);
        }

        public long setAndGetLastConnection(long newValue) {
            return (this.lastConnection = newValue);
        }

        public void blockSession(long session) {
            this.blockedSessions.add(session);
        }

        public boolean isBlocked(long session) {
            return blockedSessions.contains(session);
        }
    }

}


