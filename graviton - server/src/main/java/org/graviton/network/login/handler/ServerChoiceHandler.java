package org.graviton.network.login.handler;

import org.graviton.api.AbstractHandler;
import org.graviton.database.models.GameServer;
import org.graviton.network.exchange.state.State;
import org.graviton.network.login.LoginClient;
import org.graviton.network.login.protocol.LoginProtocol;


/**
 * Created by Botan on 30/10/2016 : 01:42
 */
public class ServerChoiceHandler extends AbstractHandler {


    ServerChoiceHandler(LoginClient client) {
        super(client);
        client.send(LoginProtocol.nicknameInformationMessage(client.getAccount().getNickname()));
        client.send(LoginProtocol.communityInformationMessage());
        client.send(LoginProtocol.serversInformationMessage(client.getGameServerRepository().getGameServers().values()));
        client.send(LoginProtocol.identificationSuccessMessage(client.getAccount().getRights() > 0));
        client.send(LoginProtocol.accountQuestionInformationMessage(client.getAccount().getSecretQuestion()));
        client.getAccount().setPlayers(client.getAccountRepository().load(client.getAccount().getId()));
    }

    @Override
    public void handle(String data) {
        switch (data.charAt(1)) {
            case 'F':
                client.send(LoginProtocol.searchPlayerMessage(client.getAccountRepository().getPlayers(data.substring(2)), client));
                break;
            case 'X':
                selectServer(Byte.parseByte(data.substring(2)));
                break;
            case 'x':
                client.send(LoginProtocol.playersListMessage(client.getAccount().getPlayers(), client.getGameServerRepository().getGameServers().values()));
                break;
        }
    }

    private void selectServer(byte gameServerId) {
        GameServer gameServer = client.getGameServerRepository().getGameServers().get(gameServerId);

        if (gameServer.getState() != State.ONLINE || gameServer.getExchangeClient() == null) {
            client.send(LoginProtocol.notAvailableGameServer());
            return;
        }

        client.send(LoginProtocol.selectGameServerMessage(client.getAccount().getId(), gameServer));
        client.getAccountRepository().getConnectedClients().put(client.getAccount().getId(), gameServerId);
    }

}
