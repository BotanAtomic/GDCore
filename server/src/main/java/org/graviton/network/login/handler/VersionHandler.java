package org.graviton.network.login.handler;

import org.graviton.api.AbstractHandler;
import org.graviton.network.login.LoginClient;
import org.graviton.network.login.LoginServer;

/**
 * Created by Botan on 29/10/2016 : 23:17
 */
public class VersionHandler extends AbstractHandler<LoginClient> {

    @Override
    public void handle(String data, LoginClient client) {
        if (data.equals(LoginServer.DOFUS_VERSION)) {
            client.setHandler(new AuthenticationHandler());
        } else {
            System.err.println("BAD");
        }
    }
}
