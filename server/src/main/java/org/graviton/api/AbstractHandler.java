package org.graviton.api;

/**
 * Created by Botan on 29/10/2016 : 23:29
 */
public abstract class AbstractHandler<C> {

    public abstract void handle(String data, C client);

}
