package org.graviton.game.group;

import org.graviton.game.client.player.Player;
import org.graviton.network.game.protocol.PartyPacketFormatter;

import java.util.concurrent.CopyOnWriteArraySet;

import static org.graviton.network.game.protocol.PartyPacketFormatter.locationMessage;

/**
 * Created by Botan on 29/01/2017. 00:36
 */
public class Group extends CopyOnWriteArraySet<Player> {
    private final Player chief;

    public Group(Player chief, Player second) {
        this.chief = chief;
        register(chief);
        register(second);

        send(PartyPacketFormatter.createMessage(this.chief.getName()));
        send(PartyPacketFormatter.informationMessage(this.chief.getId()));
        send(PartyPacketFormatter.buildPMMessage(this));
    }

    private void register(Player player) {
        add(player);
        player.setGroup(this);
    }

    public void addPlayer(Player player) {
        send(PartyPacketFormatter.singlePmMessage(player));
        register(player);
        player.send(PartyPacketFormatter.createMessage(this.chief.getName()));
        player.send(PartyPacketFormatter.informationMessage(this.chief.getId()));
        player.send(PartyPacketFormatter.buildPMMessage(this));
    }

    public void quit(Player player, Player kicker) {
        if (player.getId() == chief.getId() || size() == 2) {
            destroy();
        } else {
            remove(player);
            kick(player, kicker == null ? "" : String.valueOf(kicker.getId()));
            send(PartyPacketFormatter.singlePmMessage(player.getId()));
        }
    }

    private void destroy() {
        forEach(player -> kick(player, ""));
    }

    private void kick(Player player, String kickerId) {
        player.setGroup(null);
        player.send(PartyPacketFormatter.kickPartyMessage(kickerId));
        player.send(PartyPacketFormatter.cancelMessage());
    }

    public void send(String data) {
        forEach(player -> player.send(data));
    }

    public void locateMember(Player target) {
        target.send(locationMessage(this));
    }

    public void follow(Player player, Player toFollow, boolean follow, boolean all) {
        if (follow) {
            if (!all)
                toFollow.send(PartyPacketFormatter.followClientMessage(player.getName(), false));
            player.send(PartyPacketFormatter.followMessage(toFollow.getId()));
            player.send(PartyPacketFormatter.flagMessage(toFollow.getMap().getPosition()));
            toFollow.getFollowers().add(player);
        } else {
            toFollow.send(PartyPacketFormatter.unfollowClientMessage(player.getName()));
            player.send(PartyPacketFormatter.unfollowMessage());
            player.send(PartyPacketFormatter.flagMessage("|"));
            toFollow.getFollowers().remove(player);
        }
    }

    public void followAll(Player toFollow, boolean follow) {
        if (follow) {
            toFollow.send(PartyPacketFormatter.followClientMessage("", true));
            stream().filter(player -> player != toFollow).forEach(player -> follow(player, toFollow, true, true));
        } else
            stream().filter(player -> player != toFollow).forEach(player -> follow(player, toFollow, false, true));
    }

}
