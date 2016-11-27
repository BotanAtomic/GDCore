package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GameProtocol;

import static org.graviton.utils.StringUtils.randomPseudo;

/**
 * Created by Botan on 22/11/2016 : 21:39
 */
@Slf4j
public class AccountHandler {
    private final GameClient client;

    public AccountHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, byte subHeader) { // 'A'
        switch (subHeader) {
            case 65: // 'A'
                client.createPlayer(data);
                break;

            case 68: // 'D'
                client.deletePlayer(Integer.parseInt(data.split("\\|")[0]), data.split("\\|")[1]);
                break;

            case 76: // 'L'
                client.send(client.getAccount().getPlayerPacket(true));
                break;

            case 80: // 'P'
                client.send(GameProtocol.playerNameSuggestionSuccessMessage(randomPseudo()));
                break;

            case 83: // 'S'
                client.selectPlayer(Integer.parseInt(data));
                break;

            case 84: // 'T'
                client.applyTicket(Integer.parseInt(data));
                break;

            case 86: // 'V'
                client.send(GameProtocol.requestRegionalVersionMessage());
                break;

            case 102: // 'f'
                client.send(GameProtocol.getQueuePositionMessage());
                break;

            case 103: // 'g'
                client.setLanguage(data);
                break;

            default:
                log.error("not implemented account packet '{}'", subHeader);
        }

    }


}
