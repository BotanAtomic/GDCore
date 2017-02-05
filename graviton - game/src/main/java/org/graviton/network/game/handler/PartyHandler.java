package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.client.player.Player;
import org.graviton.game.group.Group;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.PartyPacketFormatter;

/**
 * Created by Botan on 29/01/2017. 11:43
 */

@Slf4j
public class PartyHandler {
    private final GameClient client;

    public PartyHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, byte subHeader) { // 'P'
        switch (subHeader) {
            case 65: // 'A'
                accept();
                break;

            case 70: // 'F'
                client.getPlayer().getGroup().follow(client.getPlayer(), client.getPlayerRepository().find(Integer.parseInt(data.substring(1))), data.charAt(0) == '+', false);
                break;

            case 71: // 'G'
                client.getPlayer().getGroup().followAll(client.getPlayerRepository().find(Integer.parseInt(data.substring(1))), data.charAt(0) == '+');
                break;

            case 73: // 'I'
                partyInvite(data);
                break;

            case 82: // 'R'
                refuse();
                break;

            case 86: // 'V'
                kick(data);
                break;

            case 87: // 'W'
                client.getPlayer().getGroup().locateMember(client.getPlayer());
                break;

            default:
                log.error("not implemented party packet '{}'", (char) subHeader);
        }
    }

    private void kick(String data) {
        Player target = data.isEmpty() ? client.getPlayer() : client.getPlayerRepository().find(Integer.parseInt(data));
        client.getPlayer().getGroup().quit(target, target.getId() == client.getPlayer().getId() ? null : client.getPlayer());
    }

    private void partyInvite(String name) {
        Player target = client.getPlayerRepository().find(name);

        if (target == null)
            client.send(PartyPacketFormatter.errorMessage("n" + name));
        else if (target.getGroup() != null)
            client.send(PartyPacketFormatter.errorMessage("a" + name));
        else if (client.getPlayer().getGroup() != null && client.getPlayer().getGroup().size() >= 8)
            client.send(PartyPacketFormatter.errorMessage("f"));
        else {
            target.send(PartyPacketFormatter.invitationMessage(client.getPlayer().getName(), target.getName()));
            client.send(PartyPacketFormatter.invitationMessage(client.getPlayer().getName(), target.getName()));

            target.interactionManager().setInteractionWith(client.getPlayer().getId());
            client.getInteractionManager().setInteractionWith(target.getId());
        }
    }

    private void accept() {
        Player target = client.getPlayerRepository().find(client.getInteractionManager().getInteractionCreature());

        if (target != null) {
            target.send(PartyPacketFormatter.cancelMessage());
            target.interactionManager().setInteractionWith(0);
            client.getInteractionManager().setInteractionWith(0);

            if (target.getGroup() == null) {
                new Group(target, client.getPlayer());
            } else {
                target.getGroup().addPlayer(client.getPlayer());
            }
        }

    }

    private void refuse() {
        Player target = client.getPlayerRepository().find(client.getInteractionManager().getInteractionCreature());

        if (target != null) {
            target.send(PartyPacketFormatter.cancelMessage());
            target.interactionManager().setInteractionWith(0);
        }
        client.getInteractionManager().setInteractionWith(0);
    }


}
