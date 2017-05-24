package org.graviton.network.login.handler;

import org.graviton.api.AbstractHandler;
import org.graviton.network.login.LoginClient;
import org.graviton.network.login.LoginServer;
import org.graviton.network.login.protocol.LoginProtocol;

/**
 * Created by Botan on 29/10/2016 : 23:17
 */
public class VersionHandler extends AbstractHandler {

    public VersionHandler(LoginClient client) {
        super(client);
    }

    @Override
    public void handle(String data) {
        if (data.equals(LoginServer.DOFUS_VERSION))
            client.setHandler(new AuthenticationHandler(client));
        else
            client.send(LoginProtocol.badClientVersion(LoginServer.DOFUS_VERSION));
    }
}
