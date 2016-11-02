package org.graviton.api;

import org.graviton.network.login.LoginClient;

/**
 * Created by Botan on 29/10/2016 : 23:29
 */
public abstract class AbstractHandler {

    public abstract void handle(String data, LoginClient client);

}
