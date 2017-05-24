package org.graviton.network.login.handler;

import org.graviton.api.AbstractHandler;
import org.graviton.network.login.LoginClient;
import org.graviton.network.login.LoginServer;
import org.graviton.network.login.protocol.LoginProtocol;

import java.util.Arrays;


/**
 * Created by Botan on 30/10/2016 : 01:46
 */
public class NicknameInputHandler extends AbstractHandler {

    public NicknameInputHandler(LoginClient client) {
        super(client);
        client.send(LoginProtocol.emptyNickname());
    }

    @Override
    public void handle(String data) {
        if (data.equals("Af")) return;

        if (containsForbiddenString(data) || !client.getAccountRepository().isAvailableNickname(data))
            client.send(LoginProtocol.notAvailableNickname());
        else {
            client.getAccount().setNickname(data);
            client.getAccountRepository().updateNickname(client.getAccount());
            client.setHandler(new ServerChoiceHandler(client));
        }
    }

    private static boolean containsForbiddenString(String data) {
        return Arrays.stream(LoginServer.FORBIDDEN_WORD.split(",")).filter(data::contains).count() > 0;
    }

}
