package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.channel.Channel;
import org.graviton.game.client.player.Player;
import org.graviton.game.command.api.AbstractCommand;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;

import java.util.Date;

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
            case 65: // 'A'
                launchCommand(data);
                break;

            case 68: // 'D'
                client.send(GamePacketFormatter.serverTimeMessage(new Date().getTime()));
                break;

            case 77: // 'M'
                speak(data.split("\\|"));
                break;

            case 83: // 'S'
                client.getPlayer().getMap().send(GamePacketFormatter.emoteMessage(client.getPlayer().getId(), data));
                break;

            case 97: // 'a'
                client.getPlayer().changeMap(client.getEntityFactory().getMap(data.substring(1)), (short) 0);
                break;

            default:
                log.error("not implemented basic packet '{}'", (char) subHeader);
        }
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
                client.getPlayer().getMap().send(generatedPacket);
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

    private void launchCommand(String data) {
        AbstractCommand command = client.getEntityFactory().getCommand(data.split(" ")[0].toLowerCase());
        if (command != null)
            command.apply(client.getPlayer(), data);
        else
            client.send(MessageFormatter.redConsoleMessage("Cannot find command '" + data.split(" ")[0] + "', tap help for all commands"));
    }
}
