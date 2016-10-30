package org.graviton.network.login.handler;

import org.graviton.api.AbstractHandler;
import org.graviton.database.models.Account;
import org.graviton.network.login.LoginClient;
import org.graviton.network.login.protocol.LoginProtocol;
import org.graviton.utils.StringUtils;

/**
 * Created by Botan on 29/10/2016 : 23:44
 */
public class AuthenticationHandler extends AbstractHandler {

    @Override
    public void handle(String data, LoginClient client) {
        data = data.replace("\n", ";");

        Account account = client.getAccountRepository().load(data.split(";")[0]);

        if (account != null && StringUtils.encryptPassword(account.getPassword(), client.getKey()).equals(data.split(";")[1])) {
            client.getAccountRepository().register(account);
            client.setHandler(account.getNickname().isEmpty() ? new NicknameInputHandler(client) : new PlayerSelectionHandler());
        } else
            client.send(LoginProtocol.accessDenied());

    }

}