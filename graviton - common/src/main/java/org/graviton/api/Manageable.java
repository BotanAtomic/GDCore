package org.graviton.api;

/**
 * Created by Botan on 29/10/2016 : 20:36
 */
public interface Manageable {
    void start();

    void stop();

    byte index();
}
