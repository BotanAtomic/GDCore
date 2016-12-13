package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.channel.Channel;
import org.graviton.game.client.player.Player;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;

/**
 * Created by Botan on 03/12/2016. 16:21
 */

@Slf4j
public class BasicHandler {
    private final GameClient client;

    public BasicHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, byte subHeader) { // 'B'
        switch (subHeader) {
            case 77: // 'M'
                speak(data.split("\\|"));
                break;

            case 83: // 'S'
                client.getPlayer().getGameMap().send(GamePacketFormatter.emoteMessage(client.getPlayer().getId(), data));
                break;

            case 97: // 'a'
                changePlayerMapByPosition(data.substring(1));
                break;

            default:
                log.error("not implemented basic packet '{}'", (char) subHeader);
        }
    }


    private void changePlayerMapByPosition(String position) {
        client.getPlayer().changeMap(client.getEntityFactory().getMapByPosition(position), (short) 0);
    }

    private void speak(String[] data) {
        Player player = client.getPlayer();

        Channel channel = Channel.get(data[0].charAt(0));

        if (channel == null) {
            Player target = client.getPlayerRepository().find(data[0]);
            if (player != null) {
                target.send(MessageFormatter.privateMessage(player.getId(), player.getName(), data[1], true));
                client.send(MessageFormatter.privateMessage(target.getId(), target.getName(), data[1], false));
            } else
                client.send(MessageFormatter.notConnectedPlayerMessage(data[0]));
        } else
            speak(channel, MessageFormatter.buildChannelMessage(channel.value(), player, data[1]));
    }

    private void speak(Channel channel, String generatedPacket) {
        switch (channel) {
            case General:
                client.getPlayer().getGameMap().send(generatedPacket);
                break;

            case Trade:
            case Recruitment:
                client.getPlayerRepository().send(generatedPacket);
                break;

            case Admin:
            case Information:
            case Alignment:
            case Team:
            case Party:
            case Guild:
                break;
        }
    }
}
