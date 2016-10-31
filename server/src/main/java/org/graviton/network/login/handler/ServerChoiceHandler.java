package org.graviton.network.login.handler;

import org.graviton.api.AbstractHandler;
import org.graviton.database.models.GameServer;
import org.graviton.network.exchange.protocol.ExchangeProtocol;
import org.graviton.network.exchange.state.State;
import org.graviton.network.login.LoginClient;
import org.graviton.network.login.protocol.LoginProtocol;


/**
 * Created by Botan on 30/10/2016 : 01:42
 */
public class ServerChoiceHandler extends AbstractHandler {

    public ServerChoiceHandler(LoginClient client) {
        client.send(LoginProtocol.nicknameInformationMessage(client.getAccount().getNickname()));
        client.send(LoginProtocol.communityInformationMessage(0));
        client.send(LoginProtocol.serversInformationsMessage(client.getGameServerRepository().getGameServers().values()));
        client.send(LoginProtocol.identificationSuccessMessage(client.getAccount().getRights() > 0));
        client.send(LoginProtocol.accountQuestionInformationMessage(client.getAccount().getSecretQuestion()));
        client.getAccount().setPlayers(client.getAccountRepository().load(client.getAccount().getId()));
    }

    @Override
    public void handle(String data, LoginClient client) {
        switch (data.substring(1, 2)) {
            case "F":
                client.send(LoginProtocol.searchPlayerMessage(client.getAccountRepository().getPlayers(data.substring(2)), client));
                break;
            case "X":
                selectServer(client, Byte.parseByte(data.substring(2)));
                break;
            case "x":
                client.send(LoginProtocol.playersListMessage(client.getAccount().getPlayers(), client.getGameServerRepository().getGameServers().values()));
                break;
        }
    }

    private void selectServer(LoginClient client, byte gameServerId) {
        GameServer gameServer = client.getGameServerRepository().getGameServers().get(gameServerId);

        if (gameServer.getState() != State.ONLINE) {
            client.send(LoginProtocol.notAvailableGameServer());
            return;
        }

        gameServer.getExchangeClient().send(ExchangeProtocol.connectAccount(client.getAccount().getId()));
        client.send(LoginProtocol.selectGameServerMessage(client.getAccount().getId(), gameServer));
        client.getAccountRepository().getConnectedClients().put(client.getAccount().getId(), gameServerId);
    }

}
