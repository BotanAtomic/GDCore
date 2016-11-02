package org.graviton.network.login.handler;

import org.graviton.api.AbstractHandler;
import org.graviton.network.login.LoginClient;
import org.graviton.network.login.LoginServer;
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
        if (data.equals("Af")) return;

        if (containsForbiddenString(data) || !client.getAccountRepository().isAvailableNickname(data))
            client.send(LoginProtocol.notAvailableNickname());
        else {
            client.getAccount().setNickname(data);
            client.getAccountRepository().updateNickname(client.getAccount());
            client.setHandler(new ServerChoiceHandler(client));
        }
    }

    private boolean containsForbiddenString(String data) {
        for (String word : LoginServer.FORBIDDEN_WORD.split(","))
            if (data.contains(word))
                return true;
        return false;
    }

}
