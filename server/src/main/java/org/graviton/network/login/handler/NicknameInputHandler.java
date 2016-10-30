package org.graviton.network.login.handler;

import org.graviton.api.AbstractHandler;
import org.graviton.network.login.LoginClient;
import org.graviton.network.login.protocol.LoginProtocol;

/**
 * Created by Botan on 30/10/2016 : 01:46
 */
public class NicknameInputHandler extends AbstractHandler {

    public NicknameInputHandler(LoginClient client) {
        client.send(LoginProtocol.emptyNickname());
    }

    @Override
    public void handle(String data, LoginClient client) {

    }

}
