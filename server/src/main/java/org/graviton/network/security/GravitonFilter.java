package org.graviton.network.security;


import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Botan on 03/04/2016.
 */
@Slf4j
public class GravitonFilter extends IoFilterAdapter {

    private final byte maxConnexion;
    private final short delay;

    private final Map<String, IpInstance> data;
    private final ReentrantLock locker;

    public GravitonFilter(byte connexion, short delay) {
        this.maxConnexion = connexion;
        this.delay = delay;
        this.data = new HashMap<>();
        this.locker = new ReentrantLock();
    }


    /**
     * Check the number of connection & the time
     *
     * @param ipInstance, instance of player's address informations
     * @param address,    address (IP)
     * @param session,    @IoSession
     * @param difference, difference of time with last connection
     * @return if is attacker
     */
    private boolean isAttack(IpInstance ipInstance, String address, IoSession session, long difference) {
        if (ipInstance.isBanned()) {
            if (difference < 200) {
                if (ipInstance.getConnection().get() > 10) {
                    log.error("The system has detected an attack from the address {} with {} ms of interval", address, difference == 0 ? 1 : difference);
                    ipInstance.setAttacker(true);
                }
                return true;
            }
            session.write("AlEb");
            return true;
        }
        return false;
    }


    public boolean isBlocked(IoSession session) {
        locker.lock();
        try {
            String address = session.getRemoteAddress().toString().split(":")[0].substring(1);
            IpInstance ipInstance = search(address);
            ipInstance.getConnection().incrementAndGet();

            if (ipInstance.isAttacker())
                return true;

            long newTime = System.currentTimeMillis();
            long difference = newTime - ipInstance.getLastConnection().getAndSet(newTime);

            if (isAttack(ipInstance, address, session, difference))
                return true;

            if (difference < (delay * 1000)) {
                if (ipInstance.getConnection().get() > maxConnexion)
                    if (ipInstance.addWarning())
                        ipInstance.setAttacker(true);
                return true;
            } else {
                ipInstance.setConnection(new AtomicInteger(0));
                ipInstance.setWarning(0);
            }
            return false;
        } finally {
            locker.unlock();
        }
    }

    private IpInstance search(String ip) {
        IpInstance ipInstance;
        if (!data.containsKey(ip))
            data.put(ip, ipInstance = new IpInstance());
        else
            ipInstance = data.get(ip);
        return ipInstance;
    }

    @Override
    public void sessionCreated(NextFilter nextFilter, IoSession session) {
        if (!isBlocked(session))
            nextFilter.sessionCreated(session);
        else
            session.closeNow();
    }

    @Override
    public void sessionOpened(NextFilter nextFilter, IoSession session) throws Exception {
        nextFilter.sessionOpened(session);
    }

    @Override
    public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
        if (!search(session.getRemoteAddress().toString().split(":")[0].substring(1)).isAttacker())
            nextFilter.sessionClosed(session);
        else
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

}


